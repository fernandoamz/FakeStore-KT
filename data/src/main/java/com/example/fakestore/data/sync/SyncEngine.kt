package com.example.fakestore.data.sync

import android.net.http.HttpException
import com.example.fakestore.data.local.dao.MessageDao
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class SyncEngine @Inject constructor(
    private val messageDao: MessageDao
) {
    suspend fun syncPendingMessages() {
        val pendingOperations = messageDao.getPendingOutboxOperations()

        // Sync each operation
        for ((id, messageId, _, _, _, retryCount) in pendingOperations) {
            val message = messageDao.getMessageById(messageId) ?: continue

            try {
                // 1. Change the status to sending
                messageDao.updateMessageFailure(message.id, "SENDING", message.retryCount, null)

                // 2. Send the message call retrofit to send the  mssage
                val serverMessageId = "server_${System.currentTimeMillis()}"

                // 3. Success, update the message to SENT and erase outbox
                messageDao.updateMessageSuccess(message.id, "SENT", serverMessageId)
                messageDao.deleteOutbox(id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val errorMsg = e.message ?: e.localizedMessage ?: "Unknown error"
                val isServerUnavailable = errorMsg.contains("503") || errorMsg.contains("500") || errorMsg.contains("504")
                val newRetryCount = message.retryCount + 1

                if (isServerUnavailable && newRetryCount < 5) {
                    // Error transitorio del servidor: se mantiene en PENDING para reintentar
                    messageDao.updateMessageFailure(message.id, "PENDING", newRetryCount, "Transient Error: $errorMsg")
                } else {
                    // Error permanente o límite de reintentos alcanzado: marca como FAILED y remueve del outbox
                    messageDao.updateMessageFailure(message.id, "FAILED", newRetryCount, "Permanent Error: $errorMsg")
                    messageDao.deleteOutbox(id)
                }
            } catch (e: IOException) {
                // Fallos de red / sin internet (Transitorios)
                val newRetryCount = message.retryCount + 1
                val status = if (newRetryCount >= 5) "FAILED" else "PENDING"
                messageDao.updateMessageFailure(message.id, status, newRetryCount, "Network Error: ${e.localizedMessage}")
            } catch (e: Exception) {
                val newRetryCount = retryCount + 1
                val status = if (newRetryCount >= 5) "FAILED" else "PENDING"
                messageDao.updateMessageFailure(message.id, status, newRetryCount, e.localizedMessage)
            }
        }
    }
}