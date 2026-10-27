package com.example.fakestore.feature.products.llm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.ChatMessage
import com.example.fakestore.domain.model.ChatRole
import com.example.fakestore.domain.usecase.GetCategoriesUseCase
import com.example.fakestore.domain.usecase.GetProductsUseCase
import com.example.fakestore.domain.usecase.SendAssistantMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LLMViewModel @Inject constructor(
    private val sendAssistantMessageUseCase: SendAssistantMessageUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LLMUiState())
    val uiState: StateFlow<LLMUiState> = _uiState.asStateFlow()

    init {
        refreshSuggestions()
    }

    fun sendMessage(rawMessage: String) {
        val message = rawMessage.trim()
        if (message.isEmpty() || _uiState.value.isSending) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = ChatRole.USER,
            content = message
        )

        val historyBeforeSend = _uiState.value.messages
        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                isSending = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            val result = sendAssistantMessageUseCase(
                message = message,
                history = historyBeforeSend
            )

            result.onSuccess { reply ->
                val assistantMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    role = ChatRole.ASSISTANT,
                    content = reply
                )
                _uiState.update {
                    it.copy(
                        messages = it.messages + assistantMessage,
                        isSending = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSending = false,
                        errorMessage = error.message
                            ?: "No se pudo contactar al asistente. Revisa ASSISTANT_BASE_URL y Firebase Functions."
                    )
                }
            }
        }
    }

    fun clearConversation() {
        val suggestions = _uiState.value.suggestions
        _uiState.value = LLMUiState(suggestions = suggestions)
        refreshSuggestions()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun refreshSuggestions() {
        viewModelScope.launch {
            val categories = runCatching { getCategoriesUseCase() }.getOrDefault(emptyList())
            val sampleTitles = runCatching {
                getProductsUseCase().first().shuffled().take(3).map { it.title }
            }.getOrDefault(emptyList())

            _uiState.update {
                it.copy(suggestions = buildAssistantSuggestions(categories, sampleTitles))
            }
        }
    }
}
