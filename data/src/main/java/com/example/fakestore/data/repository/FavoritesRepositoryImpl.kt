package com.example.fakestore.data.repository

import com.example.fakestore.domain.repository.FavoritesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@Singleton
class FavoritesRepositoryImpl @Inject constructor() : FavoritesRepository {

    private val _favoriteProductIds = MutableStateFlow<Set<Int>>(emptySet())
    override val favoriteProductIds: Flow<Set<Int>> = _favoriteProductIds.asStateFlow()

    override suspend fun toggleFavorite(productId: Int) {
        _favoriteProductIds.update { favorites ->
            val mutable = favorites.toMutableSet()
            if (mutable.contains(productId)) {
                mutable.remove(productId)
            } else {
                mutable.add(productId)
            }
            mutable
        }
    }

    override fun isFavorite(productId: Int): Flow<Boolean> {
        return _favoriteProductIds.map { it.contains(productId) }
    }
}
