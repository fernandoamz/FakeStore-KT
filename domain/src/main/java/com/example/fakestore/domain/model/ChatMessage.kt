package com.example.fakestore.domain.model

enum class ChatRole {
    USER,
    ASSISTANT
}

data class ChatMessage(
    val id: String,
    val role: ChatRole,
    val content: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
