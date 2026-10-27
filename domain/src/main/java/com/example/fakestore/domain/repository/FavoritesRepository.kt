package com.example.fakestore.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    val favoriteProductIds: Flow<Set<Int>>
    suspend fun toggleFavorite(productId: Int)
    fun isFavorite(productId: Int): Flow<Boolean>
}
