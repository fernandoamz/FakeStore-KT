package com.example.fakestore.data.sync

import com.example.fakestore.data.local.dao.CartDao
import com.example.fakestore.core.network.CartApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartSyncEngine @Inject constructor(
    private val cartDao: CartDao,
    private val cartApi: CartApi
) {
    suspend fun syncPendingOperations() {
        val pendingOps = cartDao.getPendingOperations()
        for (op in pendingOps) {
            try {
                // Execute authoritative backend call
                // cartApi.syncOperation(...)
                cartDao.deleteOperation(op.id)
            } catch (e: Exception) {
                // Handle retry policies
            }
        }
    }
}
