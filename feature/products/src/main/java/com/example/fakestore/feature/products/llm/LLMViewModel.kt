package com.example.fakestore.feature.products.llm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.local.dao.MessageDao
import com.example.fakestore.data.local.entity.MessageEntity
import com.example.fakestore.domain.model.ChatMessage
import com.example.fakestore.domain.model.ChatRole
import com.example.fakestore.domain.usecase.GetCategoriesUseCase
import com.example.fakestore.domain.usecase.GetProductsUseCase
import com.example.fakestore.domain.usecase.SendAssistantMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

private const val AI_CONVERSATION_ID = "ai_assistant_conv"

@HiltViewModel
class LLMViewModel @Inject constructor(
    private val sendAssistantMessageUseCase: SendAssistantMessageUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val messageDao: MessageDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(LLMUiState())
    val uiState: StateFlow<LLMUiState> = _uiState.asStateFlow()

    val dbMessages: StateFlow<List<ChatMessage>> = messageDao.observeMessages(AI_CONVERSATION_ID)
        .map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    role = if (entity.senderId == "user_me") ChatRole.USER else ChatRole.ASSISTANT,
                    content = entity.content
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        refreshSuggestions()
    }

    fun sendMessage(rawMessage: String) {
        val message = rawMessage.trim()
        if (message.isEmpty() || _uiState.value.isSending) return

        val localId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val userMessageEntity = MessageEntity(
            id = localId,
            clientMessageId = UUID.randomUUID().toString(),
            conversationId = AI_CONVERSATION_ID,
            senderId = "user_me",
            content = message,
            createdAt = timestamp,
            serverMessageId = null,
            deliveryStatus = "SENT",
            retryCount = 0,
            lastError = null
        )

        viewModelScope.launch {
            messageDao.insertMessage(userMessageEntity)
            _uiState.update { it.copy(isSending = true, errorMessage = null) }

            val MAX_HISTORY = 10
            val currentEntities = messageDao.observeMessages(AI_CONVERSATION_ID).first()
            val historyBeforeSend = currentEntities.takeLast(MAX_HISTORY).map {
                ChatMessage(
                    id = it.id,
                    role = if (it.senderId == "user_me") ChatRole.USER else ChatRole.ASSISTANT,
                    content = it.content
                )
            }

            val result = sendAssistantMessageUseCase(
                message = message,
                history = historyBeforeSend.dropLast(1)
            )

            result.onSuccess { reply ->
                val assistantMessageEntity = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    clientMessageId = UUID.randomUUID().toString(),
                    conversationId = AI_CONVERSATION_ID,
                    senderId = "assistant_ai",
                    content = reply,
                    createdAt = System.currentTimeMillis(),
                    serverMessageId = null,
                    deliveryStatus = "SENT",
                    retryCount = 0,
                    lastError = null
                )
                messageDao.insertMessage(assistantMessageEntity)
                _uiState.update { it.copy(isSending = false, errorMessage = null) }
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
                val products = withTimeoutOrNull(2000L) {
                    getProductsUseCase().first()
                } ?: emptyList()
                products.shuffled().take(3).map { it.title }
            }.getOrDefault(emptyList())

            _uiState.update {
                it.copy(suggestions = buildAssistantSuggestions(categories, sampleTitles))
            }
        }
    }
}
