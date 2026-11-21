package com.example.fakestore.feature.products.search

import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.model.SearchHistory

data class SearchUiState(
    val query: String = "",
    val recentSearches: List<SearchHistory> = emptyList(),
    val searchResults: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface SearchIntent {
    data class QueryChanged(val query: String) : SearchIntent
    data class SearchSubmitted(val query: String) : SearchIntent
    data class RecentSearchSelected(val query: String) : SearchIntent
    data class RemoveRecentSearch(val id: String) : SearchIntent
    data object ClearSearchHistory : SearchIntent
    data object RetrySearch : SearchIntent
}
