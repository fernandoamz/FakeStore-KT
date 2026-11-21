package com.example.fakestore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fakestore.data.local.entity.MessageEntity
import com.example.fakestore.data.local.entity.MessageOutboxEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE conversation_id = :conversationId ORDER BY created_at ASC")
    fun observeMessages(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMessageOutbox(message: MessageOutboxEntity)

    @Query("SELECT * FROM message_outbox WHERE status = 'PENDING'")
    suspend fun getPendingOutboxOperations(): List<MessageOutboxEntity>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    @Query("UPDATE messages SET delivery_status = :status, server_message_id = :serverId WHERE id = :messageId")
    suspend fun updateMessageSuccess(messageId: String, status: String, serverId: String)

    @Query("UPDATE messages SET delivery_status = :status, retry_count = :retryCount, last_error = :error WHERE id = :messageId")
    suspend fun updateMessageFailure(messageId: String, status: String, retryCount: Int, error: String?)

    @Query("DELETE FROM message_outbox WHERE id = :outboxId")
    suspend fun deleteOutbox(outboxId: String)
}
