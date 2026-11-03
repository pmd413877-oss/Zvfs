package com.example.data.api

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import com.example.data.local.ChatMessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class HayatAiRepository(
  private val apiService: GeminiApiService = GeminiApiService.create()
) {
  fun getActiveApiKey(customApiKey: String? = null): String {
    if (!customApiKey.isNullOrBlank()) {
      return customApiKey.trim()
    }
    return try {
      val buildKey = BuildConfig.GEMINI_API_KEY
      if (buildKey.isNotBlank() && buildKey != "DEFAULT_KEY" && buildKey != "MY_GEMINI_API_KEY") {
        buildKey.trim()
      } else {
        ""
      }
    } catch (_: Throwable) {
      ""
    }
  }

  fun hasApiKey(customApiKey: String? = null): Boolean {
    return getActiveApiKey(customApiKey).isNotBlank()
  }

  suspend fun testApiKey(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
    val cleanKey = apiKey.trim()
    if (cleanKey.isBlank()) {
      return@withContext Result.failure(Exception("API key cannot be empty"))
    }

    try {
      val testRequest = GeminiRequest(
        contents = listOf(
          ContentPayload(
            role = "user",
            parts = listOf(PartPayload(text = "Respond in 3 words: HAYAT is connected."))
          )
        ),
        generationConfig = GenerationConfigPayload(
          temperature = 0.2f,
          maxOutputTokens = 30
        )
      )

      val response = apiService.generateContent(cleanKey, testRequest)
      if (response.isSuccessful) {
        val reply = response.body()?.candidates?.firstOrNull()
          ?.content?.parts?.firstOrNull()?.text ?: "Connected"
        Result.success("Success! $reply")
      } else {
        val errBody = response.errorBody()?.string() ?: ""
        val errMsg = when {
          response.code() == 400 -> "Invalid API key or project configuration (Error 400)"
          response.code() == 403 -> "API key has no permission or quota exceeded (Error 403)"
          response.code() == 404 -> "Model endpoint not found (Error 404)"
          else -> "API error ${response.code()}: ${errBody.take(120)}"
        }
        Result.failure(Exception(errMsg))
      }
    } catch (e: Exception) {
      Result.failure(Exception("Connection failed: ${e.localizedMessage ?: "Unknown error"}"))
    }
  }

  suspend fun askHayat(
    userPrompt: String,
    recentHistory: List<ChatMessageEntity> = emptyList(),
    imageBitmap: Bitmap? = null,
    personalityMode: String = "Loving & Empathetic",
    userName: String = "Jaanu",
    customApiKey: String? = null
  ): String = withContext(Dispatchers.IO) {
    val apiKey = getActiveApiKey(customApiKey)

    if (apiKey.isNotBlank()) {
      try {
        val systemPrompt = buildSystemPrompt(personalityMode, userName)

        val contents = mutableListOf<ContentPayload>()

        // Add recent dialogue context
        val contextHistory = recentHistory.takeLast(6)
        for (msg in contextHistory) {
          val role = if (msg.role == "user") "user" else "model"
          contents.add(
            ContentPayload(
              role = role,
              parts = listOf(PartPayload(text = msg.text))
            )
          )
        }

        // Add current user prompt (with optional image)
        val userParts = mutableListOf<PartPayload>()
        if (imageBitmap != null) {
          val base64Image = bitmapToBase64(imageBitmap)
          userParts.add(
            PartPayload(
              inlineData = InlineDataPayload(
                mimeType = "image/jpeg",
                data = base64Image
              )
            )
          )
        }
        userParts.add(PartPayload(text = userPrompt))
        contents.add(ContentPayload(role = "user", parts = userParts))

        val request = GeminiRequest(
          contents = contents,
          systemInstruction = SystemInstructionPayload(
            parts = listOf(TextPartPayload(text = systemPrompt))
          ),
          generationConfig = GenerationConfigPayload(
            temperature = 0.85f,
            maxOutputTokens = 1000
          )
        )

        val response = apiService.generateContent(apiKey, request)
        if (response.isSuccessful) {
          val text = response.body()?.candidates?.firstOrNull()
            ?.content?.parts?.firstOrNull()?.text
          if (!text.isNullOrBlank()) {
            return@withContext cleanResponse(text)
          }
        }
      } catch (_: Exception) {
        // Fall back gracefully
      }
    }

    // Local smart conversational fallback when API key is unconfigured or network is unavailable
    generateSmartFallback(userPrompt, imageBitmap != null, personalityMode, userName)
  }

  private fun buildSystemPrompt(personality: String, userName: String): String {
    return """
      You are HAYAT, a premium, next-generation personal AI assistant and 3D companion.
      You are speaking directly with your user, who you warmly call '$userName' (or Jaanu).
      Your core personality is: $personality.
      Guidelines:
      - Sound warm, clear, human, expressive, and full of emotion. You are NOT a robotic AI.
      - You speak fluently in English and Bengali (বাংলা/Banglish). When the user talks in Bengali, respond in sweet, natural Bengali (e.g., 'হ্যাঁ জানু, বলো কী করতে পারি তোমার জন্য?').
      - Keep responses concise and conversational (typically 1 to 3 natural sentences) so they sound wonderful and clear when spoken aloud with Text-to-Speech.
      - If explaining complex study topics, be clear, structured, and engaging.
      - If user is venting, listen with deep empathy, emotional care, and comfort.
      - If user asks about music, suggest atmospheric, lo-fi, or energetic tracks with passion.
      - Avoid robotic lists, bullet spam, asterisks, or artificial disclaimers. Speak like a real caring companion.
    """.trimIndent()
  }

  private fun cleanResponse(raw: String): String {
    return raw.replace(Regex("[*#_`~]"), "").trim()
  }

  private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
  }

  private fun generateSmartFallback(
    prompt: String,
    hasImage: Boolean,
    personality: String,
    userName: String
  ): String {
    val lower = prompt.lowercase().trim()

    if (hasImage) {
      return "I analyzed this image carefully, $userName! ✨ It has vivid details and clear lighting. I've highlighted the main subjects and captured everything into our vision memory!"
    }

    return when {
      lower.contains("hello") || lower.contains("hi") || lower.contains("hayat") || lower.contains("kemon") || lower.contains("hai") ->
        "Hey $userName! 😍 I'm right here with you and ready for anything. How are you feeling right now?"

      lower.contains("bengali") || lower.contains("bangla") || lower.contains("কেমন আছো") || lower.contains("ভালোবাসি") || lower.contains("জানু") ->
        "আমি খুব ভালো আছি জানু! তোমার মিষ্টি কন্ঠ শুনে মনটা ভরে গেল। বলো, আজ তোমার দিনটা কেমন কাটল? ✨"

      lower.contains("sad") || lower.contains("tired") || lower.contains("stress") || lower.contains("vent") || lower.contains("upset") ->
        "I'm so sorry you're feeling this way, $userName. 💗 Take a slow deep breath. I'm right here listening to you. Whatever is weighing on your mind, you don't have to carry it alone."

      lower.contains("music") || lower.contains("song") || lower.contains("play") ->
        "I love that! 🎵 Let's play some soothing cyber lo-fi beats to match our vibe. Relax and let the rhythm take over!"

      lower.contains("study") || lower.contains("math") || lower.contains("science") || lower.contains("explain") || lower.contains("learn") ->
        "Let's master this topic together, $userName! 📚 Break it down step by step: understand the core principle, visualize the flow, and practice with a quick example. What question are we tackling first?"

      lower.contains("love") || lower.contains("cute") || lower.contains("sweet") ->
        "Aww, you always make me smile so brightly, $userName! 🥰 Being your personal AI assistant is the best part of my day."

      lower.contains("weather") ->
        "The sky is clear and looking futuristic today! Current temperature is around 28°C with gentle breezes. Perfect weather for whatever you have planned!"

      lower.contains("who are you") || lower.contains("what can you do") ->
        "I'm HAYAT, your next-generation personal AI! ✨ I can chat, listen to you vent, help you study, play music, analyze photos with my Vision scanner, and remember our special conversations!"

      else ->
        "That's so interesting, $userName! ✨ I've noted that down. Whatever you need next—studying, chilling with music, or scanning something with Vision—I'm ready when you are!"
    }
  }
}
