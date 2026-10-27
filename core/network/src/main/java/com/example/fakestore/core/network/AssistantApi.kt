package com.example.fakestore.core.network

import retrofit2.http.Body
import retrofit2.http.POST

data class AssistantHistoryDto(
    val role: String,
    val content: String
)

data class AssistantChatRequestDto(
    val message: String,
    val history: List<AssistantHistoryDto> = emptyList(),
    val catalogContext: String = ""
)

data class AssistantChatResponseDto(
    val reply: String? = null,
    val error: String? = null,
    val message: String? = null
)

interface AssistantApi {
    /**
     * Gen2 Cloud Run URL is the function itself (POST "/").
     * Classic URL style still works if base ends with ".../assistantChat/".
     */
    @POST(".")
    suspend fun chat(
        @Body body: AssistantChatRequestDto
    ): AssistantChatResponseDto
}
