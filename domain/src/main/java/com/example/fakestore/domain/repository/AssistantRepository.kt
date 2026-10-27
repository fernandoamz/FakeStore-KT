package com.example.fakestore.domain.repository

import com.example.fakestore.domain.model.ChatMessage

interface AssistantRepository {
    suspend fun sendMessage(
        message: String,
        history: List<ChatMessage>,
        catalogContext: String
    ): Result<String>
}
