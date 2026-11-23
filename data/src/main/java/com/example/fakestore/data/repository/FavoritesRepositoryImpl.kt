package com.example.fakestore.data.repository

import androidx.room.withTransaction
import com.example.fakestore.data.local.room.FakeStoreDatabase
import com.example.fakestore.data.local.dao.FavoriteDao
import com.example.fakestore.data.local.entity.FavoriteEntity
import com.example.fakestore.data.local.entity.FavoriteOperationEntity
import com.example.fakestore.domain.repository.FavoritesRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val database: FakeStoreDatabase,
    private val favoriteDao: FavoriteDao
) : FavoritesRepository {

    override val favoriteProductIds: Flow<Set<Int>> =
        favoriteDao.observeFavoriteProductIds().map { it.toSet() }

    override fun isFavorite(productId: Int): Flow<Boolean> {
        return favoriteProductIds.map { it.contains(productId) }
    }

    override suspend fun toggleFavorite(productId: Int) {
        val currentFavorites = favoriteDao.getAllFavorites()
        val isCurrentlyFavorite = currentFavorites.any { it.productId == productId }
        val operationId = UUID.randomUUID().toString()
        val timestamp = System.currentTimeMillis()

        database.withTransaction {
            if (isCurrentlyFavorite) {
                favoriteDao.deleteFavoriteByProductId(productId)
                favoriteDao.insertOperation(
                    FavoriteOperationEntity(
                        id = operationId,
                        productId = productId,
                        action = "REMOVE",
                        status = "PENDING",
                        createdAt = timestamp,
                        retryCount = 0
                    )
                )
            } else {
                favoriteDao.insertFavorite(
                    FavoriteEntity(
                        id = UUID.randomUUID().toString(),
                        productId = productId,
                        addedAt = timestamp
                    )
                )
                favoriteDao.insertOperation(
                    FavoriteOperationEntity(
                        id = operationId,
                        productId = productId,
                        action = "ADD",
                        status = "PENDING",
                        createdAt = timestamp,
                        retryCount = 0
                    )
                )
            }
        }
    }
}
