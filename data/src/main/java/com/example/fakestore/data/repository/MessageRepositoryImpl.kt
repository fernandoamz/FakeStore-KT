package com.example.fakestore.data.repository

import androidx.room.withTransaction
import com.example.fakestore.data.local.room.FakeStoreDatabase
import com.example.fakestore.data.local.dao.MessageDao
import com.example.fakestore.data.local.entity.MessageEntity
import com.example.fakestore.data.local.entity.MessageOutboxEntity

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val database: FakeStoreDatabase,
    private val messageDao: MessageDao
) {
    suspend fun sendMessage(conversationId: String, senderId: String, content: String) {
        val localMessageId = UUID.randomUUID().toString()
        val clientMessageId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        val messageEntity = MessageEntity(
            id = localMessageId,
            clientMessageId = clientMessageId,
            conversationId = conversationId,
            senderId = senderId,
            content = content,
            createdAt = timestamp,
            serverMessageId = null,
            deliveryStatus = "PENDING",
            retryCount = 0,
            lastError = null
        )

        val outboxEntity = MessageOutboxEntity(
            id = UUID.randomUUID().toString(),
            messageId = localMessageId,
            operationType = "SEND_MESSAGE",
            status = "PENDING",
            createdAt = timestamp,
            retryCount = 0
        )

        // Atomic transactions if something fails won't be saved
        database.withTransaction {
            messageDao.insertMessage(messageEntity)
            messageDao.insertMessageOutbox(outboxEntity)
        }
    }
}
