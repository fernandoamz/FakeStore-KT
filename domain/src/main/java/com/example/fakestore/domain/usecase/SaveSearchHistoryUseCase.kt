package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class SaveSearchHistoryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    suspend operator fun invoke(query: String) {
        searchHistoryRepository.saveSearch(query)
    }
}
