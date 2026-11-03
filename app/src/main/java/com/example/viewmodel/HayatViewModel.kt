package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.api.HayatAiRepository
import com.example.data.local.ChatMessageEntity
import com.example.data.local.HayatDatabase
import com.example.data.local.MemoryEntity
import com.example.voice.SpeechManager
import com.example.voice.TtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ScreenTab {
  HOME, VISION, MEMORY, SETTINGS
}

enum class ActiveModal {
  NONE, MUSIC, STUDY, VENT, PHOTO, TRANSCRIPT
}

data class HayatUiState(
  val currentTab: ScreenTab = ScreenTab.HOME,
  val activeModal: ActiveModal = ActiveModal.NONE,
  val userNickname: String = "Jaanu",
  val streakCount: Int = 6,
  val hayatMood: String = "Excited",
  val moodEmoji: String = "✨",
  val assistantStatusText: String = "● Ready when you are",
  val speechPromptText: String = "Talk to Hayat...",
  val speechSubText: String = "Tap to start",
  val isListening: Boolean = false,
  val isSpeaking: Boolean = false,
  val isThinking: Boolean = false,
  val audioRms: Float = 0f,
  val lastSpokenResponse: String = "HAYAT hai!",
  val personalityMode: String = "Loving & Empathetic",
  val languagePreference: String = "Auto (English + Bengali)",
  val speechRate: Float = 1.0f,
  val speechPitch: Float = 1.03f,
  val hasApiKey: Boolean = false,
  val customApiKey: String = "",
  val isTestingApiKey: Boolean = false,
  val apiKeyTestMessage: String? = null,
  val isApiKeyTestSuccess: Boolean = false,
  val currentVoiceName: String = "Natural Human Voice",
  val availableVoices: List<String> = emptyList(),
  // Vision State
  val visionBitmap: Bitmap? = null,
  val visionAnalysisText: String = "",
  val isVisionAnalyzing: Boolean = false,
  // Memory State
  val memorySearchQuery: String = ""
)

class HayatViewModel(application: Application) : AndroidViewModel(application) {
  private val prefs = application.getSharedPreferences("hayat_prefs", Context.MODE_PRIVATE)

  private val db = Room.databaseBuilder(
    application,
    HayatDatabase::class.java,
    "hayat_ai_db"
  ).fallbackToDestructiveMigration(dropAllTables = true).build()

  private val chatDao = db.chatDao()
  private val memoryDao = db.memoryDao()
  private val aiRepository = HayatAiRepository()

  val speechManager = SpeechManager(application)
  val ttsManager = TtsManager(application)

  private val savedApiKey = prefs.getString("custom_gemini_api_key", "") ?: ""
  private val savedNickname = prefs.getString("user_nickname", "Jaanu") ?: "Jaanu"
  private val savedPersonality = prefs.getString("personality_mode", "Loving & Empathetic") ?: "Loving & Empathetic"
  private val savedLanguage = prefs.getString("language_pref", "Auto (English + Bengali)") ?: "Auto (English + Bengali)"
  private val savedPitch = prefs.getFloat("speech_pitch", 1.03f)
  private val savedRate = prefs.getFloat("speech_rate", 1.0f)

  private val _uiState = MutableStateFlow(
    HayatUiState(
      customApiKey = savedApiKey,
      hasApiKey = aiRepository.hasApiKey(savedApiKey),
      userNickname = savedNickname,
      personalityMode = savedPersonality,
      languagePreference = savedLanguage,
      speechPitch = savedPitch,
      speechRate = savedRate
    )
  )
  val uiState: StateFlow<HayatUiState> = _uiState.asStateFlow()

  val chatMessages: StateFlow<List<ChatMessageEntity>> = chatDao.getAllMessages()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val memories: StateFlow<List<MemoryEntity>> = memoryDao.getAllMemories()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    ttsManager.speechPitch = savedPitch
    ttsManager.speechRate = savedRate

    viewModelScope.launch {
      speechManager.isListening.collect { listening ->
        _uiState.value = _uiState.value.copy(
          isListening = listening,
          assistantStatusText = if (listening) "● Listening to you..." else "● Ready when you are",
          speechSubText = if (listening) "Listening..." else "Tap to start"
        )
      }
    }

    viewModelScope.launch {
      speechManager.rmsDb.collect { rms ->
        _uiState.value = _uiState.value.copy(audioRms = rms)
      }
    }

    viewModelScope.launch {
      ttsManager.isSpeaking.collect { speaking ->
        _uiState.value = _uiState.value.copy(
          isSpeaking = speaking,
          hayatMood = if (speaking) "Speaking" else if (_uiState.value.isThinking) "Thinking" else "Excited"
        )
      }
    }

    viewModelScope.launch {
      ttsManager.availableVoiceNames.collect { voices ->
        _uiState.value = _uiState.value.copy(availableVoices = voices)
      }
    }

    viewModelScope.launch {
      ttsManager.currentVoiceName.collect { voice ->
        _uiState.value = _uiState.value.copy(currentVoiceName = voice)
      }
    }

    // Seed initial welcome message if database is empty
    viewModelScope.launch {
      val recent = chatDao.getRecentMessages()
      if (recent.isEmpty()) {
        val welcome = ChatMessageEntity(
          role = "assistant",
          text = "Good evening, $savedNickname! 😍 I'm HAYAT, your personal AI companion. I'm ready whenever you are!",
          mood = "Excited"
        )
        chatDao.insertMessage(welcome)

        memoryDao.insertMemory(
          MemoryEntity(
            title = "First Activation",
            content = "HAYAT bonded with $savedNickname. Human-like voice engine & Gemini API ready.",
            category = "Personal",
            isPinned = true
          )
        )
        memoryDao.insertMemory(
          MemoryEntity(
            title = "User Preference",
            content = "Nickname: $savedNickname. Languages: English & Bengali.",
            category = "Preference",
            isPinned = true
          )
        )
      }
    }
  }

  fun getDynamicGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val period = when (hour) {
      in 5..11 -> "Good morning"
      in 12..16 -> "Good afternoon"
      in 17..21 -> "Good evening"
      else -> "Good night"
    }
    return "$period, ${_uiState.value.userNickname}😍 ✨"
  }

  fun selectTab(tab: ScreenTab) {
    _uiState.value = _uiState.value.copy(currentTab = tab)
  }

  fun setModal(modal: ActiveModal) {
    _uiState.value = _uiState.value.copy(activeModal = modal)
  }

  fun onVoiceToggle() {
    if (_uiState.value.isListening) {
      speechManager.stopListening()
    } else {
      ttsManager.stop()
      _uiState.value = _uiState.value.copy(
        speechPromptText = "Listening to your voice...",
        speechSubText = "Speak now"
      )
      speechManager.startListening(
        languagePreference = _uiState.value.languagePreference,
        onResult = { text ->
          processUserPrompt(text, isVoice = true)
        },
        onError = { error ->
          _uiState.value = _uiState.value.copy(
            speechPromptText = "Talk to Hayat...",
            speechSubText = error
          )
        }
      )
    }
  }

  fun processUserPrompt(userInput: String, isVoice: Boolean = false, imageBitmap: Bitmap? = null) {
    if (userInput.isBlank() && imageBitmap == null) return

    viewModelScope.launch {
      // 1. Save user message to Room
      chatDao.insertMessage(
        ChatMessageEntity(
          role = "user",
          text = userInput.ifBlank { "[Sent Image to Vision]" },
          isAudio = isVoice
        )
      )

      _uiState.value = _uiState.value.copy(
        isThinking = true,
        speechPromptText = "Thinking...",
        speechSubText = "Hayat is formulating an answer",
        hayatMood = "Thinking",
        moodEmoji = "🧠"
      )

      val recent = chatDao.getRecentMessages().reversed()
      val responseText = aiRepository.askHayat(
        userPrompt = userInput,
        recentHistory = recent,
        imageBitmap = imageBitmap,
        personalityMode = _uiState.value.personalityMode,
        userName = _uiState.value.userNickname,
        customApiKey = _uiState.value.customApiKey
      )

      // 2. Save assistant message
      chatDao.insertMessage(
        ChatMessageEntity(
          role = "assistant",
          text = responseText,
          mood = "Excited",
          isAudio = isVoice
        )
      )

      // Auto-extract memory if significant
      if (userInput.length > 15 && (userInput.contains("i like", true) || userInput.contains("my favorite", true) || userInput.contains("remember", true))) {
        memoryDao.insertMemory(
          MemoryEntity(
            title = "Thought & Preference",
            content = userInput,
            category = "Preference"
          )
        )
      }

      _uiState.value = _uiState.value.copy(
        isThinking = false,
        lastSpokenResponse = responseText,
        speechPromptText = "Talk to Hayat...",
        speechSubText = "Tap to start",
        hayatMood = "Loving",
        moodEmoji = "💖"
      )

      // Speak response naturally with human voice
      if (isVoice) {
        ttsManager.speak(responseText, _uiState.value.languagePreference)
      }
    }
  }

  fun speakText(text: String) {
    ttsManager.speak(text, _uiState.value.languagePreference)
  }

  fun stopSpeech() {
    ttsManager.stop()
  }

  fun setVisionImage(bitmap: Bitmap) {
    _uiState.value = _uiState.value.copy(
      visionBitmap = bitmap,
      isVisionAnalyzing = true,
      visionAnalysisText = "Hayat is analyzing the visual details..."
    )

    viewModelScope.launch {
      val analysis = aiRepository.askHayat(
        userPrompt = "Please inspect and describe this photo thoroughly. Identify key objects, mood, text, and give your friendly insight to Jaanu.",
        imageBitmap = bitmap,
        personalityMode = _uiState.value.personalityMode,
        userName = _uiState.value.userNickname,
        customApiKey = _uiState.value.customApiKey
      )

      _uiState.value = _uiState.value.copy(
        isVisionAnalyzing = false,
        visionAnalysisText = analysis
      )

      memoryDao.insertMemory(
        MemoryEntity(
          title = "Vision Scan Insight",
          content = analysis.take(160) + "...",
          category = "Vision"
        )
      )
    }
  }

  fun clearVision() {
    _uiState.value = _uiState.value.copy(
      visionBitmap = null,
      visionAnalysisText = "",
      isVisionAnalyzing = false
    )
  }

  fun saveCustomApiKey(key: String) {
    val cleanKey = key.trim()
    prefs.edit().putString("custom_gemini_api_key", cleanKey).apply()
    val hasKey = aiRepository.hasApiKey(cleanKey)
    _uiState.value = _uiState.value.copy(
      customApiKey = cleanKey,
      hasApiKey = hasKey,
      apiKeyTestMessage = if (hasKey) "API Key saved successfully!" else null,
      isApiKeyTestSuccess = hasKey
    )
  }

  fun testCustomApiKey(key: String) {
    val cleanKey = key.trim()
    if (cleanKey.isBlank()) {
      _uiState.value = _uiState.value.copy(
        isTestingApiKey = false,
        apiKeyTestMessage = "Please enter your Gemini API key first",
        isApiKeyTestSuccess = false
      )
      return
    }

    _uiState.value = _uiState.value.copy(
      isTestingApiKey = true,
      apiKeyTestMessage = "Testing connection to Gemini API..."
    )

    viewModelScope.launch {
      val result = aiRepository.testApiKey(cleanKey)
      result.onSuccess { msg ->
        // Also save on success
        prefs.edit().putString("custom_gemini_api_key", cleanKey).apply()
        _uiState.value = _uiState.value.copy(
          isTestingApiKey = false,
          customApiKey = cleanKey,
          hasApiKey = true,
          apiKeyTestMessage = "Connected to Gemini 2.5 Flash! Key is working perfectly.",
          isApiKeyTestSuccess = true
        )
      }.onFailure { err ->
        _uiState.value = _uiState.value.copy(
          isTestingApiKey = false,
          apiKeyTestMessage = err.message ?: "Failed to connect with this API key",
          isApiKeyTestSuccess = false
        )
      }
    }
  }

  fun selectTtsVoice(voiceName: String) {
    ttsManager.setVoiceByName(voiceName)
    _uiState.value = _uiState.value.copy(currentVoiceName = voiceName)
  }

  fun updateSettings(
    nickname: String = _uiState.value.userNickname,
    personality: String = _uiState.value.personalityMode,
    language: String = _uiState.value.languagePreference,
    pitch: Float = _uiState.value.speechPitch,
    rate: Float = _uiState.value.speechRate
  ) {
    prefs.edit()
      .putString("user_nickname", nickname)
      .putString("personality_mode", personality)
      .putString("language_pref", language)
      .putFloat("speech_pitch", pitch)
      .putFloat("speech_rate", rate)
      .apply()

    ttsManager.speechPitch = pitch
    ttsManager.speechRate = rate
    _uiState.value = _uiState.value.copy(
      userNickname = nickname,
      personalityMode = personality,
      languagePreference = language,
      speechPitch = pitch,
      speechRate = rate
    )
  }

  fun addManualMemory(title: String, content: String, category: String) {
    viewModelScope.launch {
      memoryDao.insertMemory(
        MemoryEntity(
          title = title,
          content = content,
          category = category
        )
      )
    }
  }

  fun deleteMemory(memory: MemoryEntity) {
    viewModelScope.launch {
      memoryDao.deleteMemory(memory)
    }
  }

  fun clearAllMemories() {
    viewModelScope.launch {
      memoryDao.clearAll()
    }
  }

  fun clearChatHistory() {
    viewModelScope.launch {
      chatDao.clearAll()
    }
  }

  override fun onCleared() {
    super.onCleared()
    ttsManager.shutdown()
    speechManager.stopListening()
  }
}
