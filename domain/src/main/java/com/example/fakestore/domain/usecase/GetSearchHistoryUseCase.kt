package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.model.SearchHistory
import com.example.fakestore.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchHistoryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    operator fun invoke(): Flow<List<SearchHistory>> {
        return searchHistoryRepository.getRecentSearches()
    }
}
