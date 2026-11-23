package com.example.fakestore.data.sync

import com.example.fakestore.data.local.dao.FavoriteDao
import com.example.fakestore.core.network.UserApi // or FavoritesApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteSyncEngine @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    suspend fun syncPendingFavorites() {
        val pendingOps = favoriteDao.getPendingOperations()

        for (op in pendingOps) {
            try {
                // Execute authoritative network call based on op.action ("ADD" or "REMOVE")
                // if (op.action == "ADD") api.addFavorite(op.productId) else api.removeFavorite(op.productId)

                // Success: delete outbox operation
                favoriteDao.deleteOperation(op.id)
            } catch (e: Exception) {
                val newRetry = op.retryCount + 1
                val status = if (newRetry >= 5) "FAILED" else "PENDING"
                favoriteDao.updateOperationStatus(op.id, status, newRetry)
            }
        }
    }
}
