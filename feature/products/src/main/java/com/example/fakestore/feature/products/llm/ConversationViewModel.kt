package com.example.fakestore.feature.products.llm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.data.local.dao.MessageDao
import com.example.fakestore.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val messageDao: MessageDao
) : ViewModel() {

    // Observamos los mensajes en tiempo real desde Room como Fuente de Verdad
    val messages: StateFlow<List<com.example.fakestore.data.local.entity.MessageEntity>> = messageDao.observeMessages("conv_123")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            messageRepository.sendMessage(
                conversationId = "conv_123",
                senderId = "user_me",
                content = content.trim()
            )
        }
    }
}
