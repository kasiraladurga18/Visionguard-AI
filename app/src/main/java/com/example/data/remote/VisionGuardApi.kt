package com.example.data.remote

import com.example.data.remote.dto.AssistantChatRequestDto
import com.example.data.remote.dto.AssistantChatResponseDto
import com.example.data.remote.dto.OcrRequestDto
import com.example.data.remote.dto.OcrResponseDto
import com.example.data.remote.dto.SosRequestDto
import com.example.data.remote.dto.SosResponseDto
import com.example.data.remote.dto.VisionAnalyzeRequestDto
import com.example.data.remote.dto.VisionAnalyzeResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VisionGuardApi {
    @POST("api/vision/analyze")
    suspend fun analyzeVision(@Body request: VisionAnalyzeRequestDto): Response<VisionAnalyzeResponseDto>

    @POST("api/vision/describe")
    suspend fun describeScene(@Body request: VisionAnalyzeRequestDto): Response<VisionAnalyzeResponseDto>

    @POST("api/ocr/read")
    suspend fun readText(@Body request: OcrRequestDto): Response<OcrResponseDto>

    @POST("api/assistant/chat")
    suspend fun chatAssistant(@Body request: AssistantChatRequestDto): Response<AssistantChatResponseDto>

    @POST("api/emergency/sos")
    suspend fun triggerSos(@Body request: SosRequestDto): Response<SosResponseDto>
}
