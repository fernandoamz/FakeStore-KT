package com.example.fakestore.feature.products.llm

import com.example.fakestore.domain.model.ChatMessage

data class LLMUiState(
    val messages: List<ChatMessage> = emptyList(),
    val suggestions: List<AssistantSuggestion> = buildAssistantSuggestions(emptyList()),
    val isSending: Boolean = false,
    val errorMessage: String? = null
) {
    val showEmptyState: Boolean
        get() = messages.isEmpty() && !isSending
}
