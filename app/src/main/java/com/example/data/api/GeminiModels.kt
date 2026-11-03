package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
  val contents: List<ContentPayload>,
  val systemInstruction: SystemInstructionPayload? = null,
  val generationConfig: GenerationConfigPayload? = null
)

@JsonClass(generateAdapter = true)
data class SystemInstructionPayload(
  val parts: List<TextPartPayload>
)

@JsonClass(generateAdapter = true)
data class ContentPayload(
  val role: String,
  val parts: List<PartPayload>
)

@JsonClass(generateAdapter = true)
data class PartPayload(
  val text: String? = null,
  val inlineData: InlineDataPayload? = null
)

@JsonClass(generateAdapter = true)
data class TextPartPayload(
  val text: String
)

@JsonClass(generateAdapter = true)
data class InlineDataPayload(
  @param:Json(name = "mime_type") val mimeType: String,
  val data: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfigPayload(
  val temperature: Float? = 0.85f,
  val maxOutputTokens: Int? = 1200
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
  val candidates: List<CandidatePayload>? = null,
  val error: GeminiErrorPayload? = null
)

@JsonClass(generateAdapter = true)
data class CandidatePayload(
  val content: ContentResponsePayload? = null
)

@JsonClass(generateAdapter = true)
data class ContentResponsePayload(
  val parts: List<ResponsePartPayload>? = null,
  val role: String? = null
)

@JsonClass(generateAdapter = true)
data class ResponsePartPayload(
  val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiErrorPayload(
  val message: String? = null,
  val code: Int? = null
)
