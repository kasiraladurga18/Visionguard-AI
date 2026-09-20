package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VisionAnalyzeRequestDto(
    @Json(name = "image_base64") val imageBase64: String,
    @Json(name = "prompt") val prompt: String? = "What is in front of me?",
    @Json(name = "task") val task: String? = "describe"
)

@JsonClass(generateAdapter = true)
data class ObstacleDto(
    @Json(name = "name") val name: String,
    @Json(name = "estimated_distance") val estimatedDistance: String,
    @Json(name = "direction") val direction: String,
    @Json(name = "hazard_level") val hazardLevel: String = "low"
)

@JsonClass(generateAdapter = true)
data class VisionAnalyzeResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "description") val description: String,
    @Json(name = "concise_speech") val conciseSpeech: String,
    @Json(name = "obstacles") val obstacles: List<ObstacleDto> = emptyList(),
    @Json(name = "detected_objects") val detectedObjects: List<String> = emptyList(),
    @Json(name = "face_detected") val faceDetected: String? = null
)

@JsonClass(generateAdapter = true)
data class OcrRequestDto(
    @Json(name = "image_base64") val imageBase64: String,
    @Json(name = "mode") val mode: String = "full",
    @Json(name = "focus_topic") val focusTopic: String? = null
)

@JsonClass(generateAdapter = true)
data class OcrResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "full_text") val fullText: String,
    @Json(name = "concise_speech") val conciseSpeech: String,
    @Json(name = "summary") val summary: String? = null,
    @Json(name = "key_sections") val keySections: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AssistantChatRequestDto(
    @Json(name = "query") val query: String,
    @Json(name = "context") val context: String? = null,
    @Json(name = "location_summary") val locationSummary: String? = null
)

@JsonClass(generateAdapter = true)
data class AssistantChatResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "answer") val answer: String,
    @Json(name = "spoken_answer") val spokenAnswer: String,
    @Json(name = "action_intent") val actionIntent: String? = null
)

@JsonClass(generateAdapter = true)
data class SosRequestDto(
    @Json(name = "latitude") val latitude: Double?,
    @Json(name = "longitude") val longitude: Double?,
    @Json(name = "address") val address: String?,
    @Json(name = "reason") val reason: String? = "Assistive vision SOS triggered"
)

@JsonClass(generateAdapter = true)
data class SosResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "sos_id") val sosId: String,
    @Json(name = "alert_message") val alertMessage: String,
    @Json(name = "contacts_notified") val contactsNotified: Int
)
