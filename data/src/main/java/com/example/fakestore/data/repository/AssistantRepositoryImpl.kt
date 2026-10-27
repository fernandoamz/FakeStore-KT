package com.example.fakestore.data.repository

import com.example.fakestore.core.network.AssistantApi
import com.example.fakestore.core.network.AssistantChatRequestDto
import com.example.fakestore.core.network.AssistantHistoryDto
import com.example.fakestore.domain.model.ChatMessage
import com.example.fakestore.domain.model.ChatRole
import com.example.fakestore.domain.repository.AssistantRepository
import javax.inject.Inject

class AssistantRepositoryImpl @Inject constructor(
    private val assistantApi: AssistantApi
) : AssistantRepository {

    override suspend fun sendMessage(
        message: String,
        history: List<ChatMessage>,
        catalogContext: String
    ): Result<String> {
        return try {
            val response = assistantApi.chat(
                AssistantChatRequestDto(
                    message = message,
                    history = history.map { item ->
                        AssistantHistoryDto(
                            role = when (item.role) {
                                ChatRole.USER -> "user"
                                ChatRole.ASSISTANT -> "assistant"
                            },
                            content = item.content
                        )
                    },
                    catalogContext = catalogContext
                )
            )

            val reply = response.reply?.trim().orEmpty()
            if (reply.isNotEmpty()) {
                Result.success(reply)
            } else {
                Result.failure(
                    IllegalStateException(
                        response.message ?: response.error ?: "Respuesta vacía del asistente"
                    )
                )
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}
