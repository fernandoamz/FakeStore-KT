package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.model.SearchHistory
import com.example.fakestore.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentSearchesUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    operator fun invoke(): Flow<List<SearchHistory>> = repository.getRecentSearches()
}

class SaveSearchQueryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String) = repository.saveSearch(query)
}

class DeleteSearchQueryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteSearch(id)
}
