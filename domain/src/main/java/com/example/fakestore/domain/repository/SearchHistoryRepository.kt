package com.example.fakestore.domain.repository

import com.example.fakestore.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getRecentSearches(): Flow<List<SearchHistory>>
    suspend fun saveSearch(query: String)
    suspend fun deleteSearch(id: String)
    suspend fun clearHistory()
}
