package com.example.fakestore.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [
        Index("conversation_id"),
        Index("client_message_id", unique = true)
    ]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "client_message_id") val clientMessageId: String,
    @ColumnInfo(name = "conversation_id") val conversationId: String,
    @ColumnInfo(name = "sender_id") val senderId: String,
    val content: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "server_message_id") val serverMessageId: String?,
    @ColumnInfo(name = "delivery_status") val deliveryStatus: String,
    @ColumnInfo(name = "retry_count") val retryCount: Int,
    @ColumnInfo(name = "last_error") val lastError: String?
)