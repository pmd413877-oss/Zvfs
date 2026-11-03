package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) {
  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _availableVoiceNames = MutableStateFlow<List<String>>(emptyList())
  val availableVoiceNames: StateFlow<List<String>> = _availableVoiceNames.asStateFlow()

  private val _currentVoiceName = MutableStateFlow("Natural Human (Default)")
  val currentVoiceName: StateFlow<String> = _currentVoiceName.asStateFlow()

  // Natural human-like conversational defaults (not metallic or robotic)
  var speechRate: Float = 1.0f
  var speechPitch: Float = 1.03f

  init {
    initTts()
  }

  private fun initTts() {
    tts = TextToSpeech(context.applicationContext) { status ->
      if (status == TextToSpeech.SUCCESS) {
        isInitialized = true
        tts?.setPitch(speechPitch)
        tts?.setSpeechRate(speechRate)

        loadBestHumanVoices()

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
          override fun onStart(utteranceId: String?) {
            _isSpeaking.value = true
          }

          override fun onDone(utteranceId: String?) {
            _isSpeaking.value = false
          }

          @Deprecated("Deprecated in Java")
          override fun onError(utteranceId: String?) {
            _isSpeaking.value = false
          }
        })
      }
    }
  }

  private fun loadBestHumanVoices() {
    try {
      val voices = tts?.voices ?: return
      val voiceList = mutableListOf<String>()

      // Best human female voice candidates
      var selectedVoice: Voice? = null
      var bestScore = -1

      for (voice in voices) {
        val name = voice.name.lowercase()
        val locale = voice.locale

        // Collect readable names for settings
        if (locale.language == "en" || locale.language == "bn") {
          voiceList.add(voice.name)
        }

        // Score voice based on human-like attributes
        var score = 0
        if (voice.quality >= Voice.QUALITY_HIGH) score += 30
        if (voice.quality >= Voice.QUALITY_VERY_HIGH) score += 40
        if (name.contains("female")) score += 50
        if (name.contains("natural") || name.contains("neural") || name.contains("wavenet") || name.contains("journey")) score += 40
        if (name.contains("en-us-x-sfg") || name.contains("en-us-x-tpd") || name.contains("en-us-x-iol")) score += 60
        if (name.contains("network")) score += 10

        if (score > bestScore) {
          bestScore = score
          selectedVoice = voice
        }
      }

      _availableVoiceNames.value = voiceList.take(20)

      if (selectedVoice != null) {
        tts?.voice = selectedVoice
        _currentVoiceName.value = selectedVoice.name
      }
    } catch (_: Throwable) {}
  }

  fun setVoiceByName(voiceName: String) {
    try {
      val voice = tts?.voices?.firstOrNull { it.name == voiceName }
      if (voice != null) {
        tts?.voice = voice
        _currentVoiceName.value = voice.name
      }
    } catch (_: Throwable) {}
  }

  fun speak(text: String, languagePreference: String = "Auto") {
    if (!isInitialized || tts == null) return

    val speechReadyText = prepareTextForHumanVoice(text)
    if (speechReadyText.isEmpty()) return

    // Auto-detect Bengali characters
    val hasBengali = speechReadyText.any { it in '\u0980'..'\u09FF' }
    if (hasBengali || languagePreference.contains("Bengali", ignoreCase = true)) {
      selectBestBengaliVoice()
    } else {
      selectBestEnglishVoice()
    }

    tts?.setPitch(speechPitch)
    tts?.setSpeechRate(speechRate)

    tts?.speak(speechReadyText, TextToSpeech.QUEUE_FLUSH, null, "HAYAT_SPEECH_${System.currentTimeMillis()}")
  }

  private fun selectBestBengaliVoice() {
    try {
      val banglaLocale = Locale.forLanguageTag("bn-BD")
      tts?.setLanguage(banglaLocale)

      // Try to find natural high quality Bengali female voice
      val bnVoice = tts?.voices?.firstOrNull {
        it.locale.language == "bn" && (it.name.contains("female", true) || it.name.contains("network", true))
      } ?: tts?.voices?.firstOrNull { it.locale.language == "bn" }

      if (bnVoice != null) {
        tts?.voice = bnVoice
      }
    } catch (_: Throwable) {
      try {
        tts?.setLanguage(Locale.forLanguageTag("bn-IN"))
      } catch (_: Throwable) {}
    }
  }

  private fun selectBestEnglishVoice() {
    try {
      tts?.setLanguage(Locale.US)
      val enVoice = tts?.voices?.firstOrNull {
        it.locale.language == "en" &&
          (it.name.contains("en-us-x-sfg", true) || it.name.contains("female", true))
      }
      if (enVoice != null) {
        tts?.voice = enVoice
      }
    } catch (_: Throwable) {}
  }

  /**
   * Cleans AI text so TextToSpeech sounds natural and clear, without pronouncing
   * markdown asterisks, raw emojis, URL links, code blocks, or brackets.
   */
  private fun prepareTextForHumanVoice(raw: String): String {
    var text = raw

    // 1. Remove code blocks and inline code
    text = text.replace(Regex("```[\\s\\S]*?```"), "")
    text = text.replace(Regex("`[^`]*`"), "")

    // 2. Remove URLs
    text = text.replace(Regex("https?://\\S+"), "")

    // 3. Remove Markdown markers (*, **, _, #, >, -, +, ~)
    text = text.replace(Regex("[*#_`~>|\\[\\]()]"), " ")

    // 4. Remove emojis that cause robotic speech descriptions
    text = text.replace(Regex("[\\p{So}\\p{Cn}]"), " ")

    // 5. Clean up duplicate punctuation and spacing
    text = text.replace(Regex("\\s+"), " ")
    text = text.replace(Regex("\\.{2,}"), ".")
    text = text.replace(Regex("!{2,}"), "!")
    text = text.replace(Regex("\\?{2,}"), "?")

    return text.trim()
  }

  fun stop() {
    tts?.stop()
    _isSpeaking.value = false
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
  }
}

class SpeechManager(private val context: Context) {
  private var speechRecognizer: SpeechRecognizer? = null

  private val _isListening = MutableStateFlow(false)
  val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

  private val _rmsDb = MutableStateFlow(0f)
  val rmsDb: StateFlow<Float> = _rmsDb.asStateFlow()

  fun startListening(
    languagePreference: String = "Auto",
    onResult: (String) -> Unit,
    onError: (String) -> Unit
  ) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
      onError("Speech recognition service not available on this device.")
      return
    }

    stopListening()

    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
      setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
          _isListening.value = true
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {
          _rmsDb.value = rmsdB
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
          _isListening.value = false
        }

        override fun onError(error: Int) {
          _isListening.value = false
          val msg = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission needed"
            SpeechRecognizer.ERROR_NETWORK -> "Network connection issue"
            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Tap to try again!"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input detected"
            else -> "Recognition issue ($error)"
          }
          onError(msg)
        }

        override fun onResults(results: Bundle?) {
          _isListening.value = false
          val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
          val spokenText = matches?.firstOrNull() ?: ""
          if (spokenText.isNotBlank()) {
            onResult(spokenText)
          }
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
      })
    }

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      if (languagePreference.contains("Bengali", ignoreCase = true)) {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
      } else {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
      }
      putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
      putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
    }

    try {
      speechRecognizer?.startListening(intent)
      _isListening.value = true
    } catch (e: Exception) {
      _isListening.value = false
      onError(e.message ?: "Could not start voice recognition")
    }
  }

  fun stopListening() {
    try {
      speechRecognizer?.stopListening()
      speechRecognizer?.destroy()
    } catch (_: Exception) {}
    speechRecognizer = null
    _isListening.value = false
    _rmsDb.value = 0f
  }
}
