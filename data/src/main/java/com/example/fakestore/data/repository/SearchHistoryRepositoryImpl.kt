package com.example.fakestore.data.repository

import com.example.fakestore.data.local.dao.SearchDao
import com.example.fakestore.data.local.entity.SearchHistoryEntity
import com.example.fakestore.domain.model.SearchHistory
import com.example.fakestore.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchDao: SearchDao
) : SearchHistoryRepository {

    override fun getRecentSearches(): Flow<List<SearchHistory>> {
        return searchDao.observeRecentSearches().map { entities ->
            entities.map {
                SearchHistory(
                    id = it.id,
                    query = it.query,
                    searchedAt = it.searchedAt
                )
            }
        }
    }

    override suspend fun saveSearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        val normalized = trimmed.lowercase()

        val entity = SearchHistoryEntity(
            id = UUID.randomUUID().toString(),
            query = trimmed,
            normalizedQuery = normalized,
            searchedAt = System.currentTimeMillis()
        )
        searchDao.insertSearchHistory(entity)
    }

    override suspend fun deleteSearch(id: String) {
        searchDao.deleteSearchHistoryById(id)
    }

    override suspend fun clearHistory() {
        searchDao.clearAllSearchHistory()
    }
}
