package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class DeleteSearchHistoryUseCase @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository
) {
    suspend operator fun invoke(id: String) {
        searchHistoryRepository.deleteSearch(id)
    }
}
