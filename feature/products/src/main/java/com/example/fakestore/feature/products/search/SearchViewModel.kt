package com.example.fakestore.feature.products.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.usecase.ClearSearchHistoryUseCase
import com.example.fakestore.domain.usecase.DeleteSearchQueryUseCase
import com.example.fakestore.domain.usecase.GetRecentSearchesUseCase
import com.example.fakestore.domain.usecase.SaveSearchQueryUseCase
import com.example.fakestore.domain.usecase.SearchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getRecentSearchesUseCase: GetRecentSearchesUseCase,
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase,
    private val deleteSearchQueryUseCase: DeleteSearchQueryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SearchUiState> = combine(
        _query,
        getRecentSearchesUseCase(),
        _searchResults,
        _isLoading,
        _error
    ) { query, recentSearches, searchResults, isLoading, error ->
        SearchUiState(
            query = query,
            recentSearches = recentSearches,
            searchResults = searchResults,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState()
    )

    init {
        viewModelScope.launch {
            _query
                .debounce(300L)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        _isLoading.value = false
                        _error.value = null
                        flowOf(emptyList())
                    } else {
                        _isLoading.value = true
                        _error.value = null
                        flow {
                            try {
                                val results = searchProductsUseCase(query)
                                emit(results)
                            } catch (e: Exception) {
                                _error.value = e.message ?: "Error searching products"
                                emit(emptyList())
                            } finally {
                                _isLoading.value = false
                            }
                        }
                    }
                }
                .collect { results ->
                    _searchResults.value = results
                }
        }
    }

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> {
                _query.value = intent.query
            }
            is SearchIntent.SearchSubmitted -> {
                if (intent.query.isNotBlank()) {
                    viewModelScope.launch {
                        saveSearchQueryUseCase(intent.query)
                    }
                }
            }
            is SearchIntent.RecentSearchSelected -> {
                _query.value = intent.query
                viewModelScope.launch {
                    saveSearchQueryUseCase(intent.query)
                }
            }
            is SearchIntent.RemoveRecentSearch -> {
                viewModelScope.launch {
                    deleteSearchQueryUseCase(intent.id)
                }
            }
            is SearchIntent.ClearSearchHistory -> {
                viewModelScope.launch {
                    clearSearchHistoryUseCase()
                }
            }
            is SearchIntent.RetrySearch -> {
                val currentQuery = _query.value
                if (currentQuery.isNotBlank()) {
                    onIntent(SearchIntent.QueryChanged(currentQuery))
                }
            }
        }
    }
}
