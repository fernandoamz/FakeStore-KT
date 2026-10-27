package com.example.fakestore.feature.products.catalog

import com.example.fakestore.domain.model.Product

sealed interface ProductsUiState {
    data object Loading : ProductsUiState

    data class Success(
        val products: List<Product> = emptyList(),
        val filteredProducts: List<Product> = emptyList(),
        val categories: List<String> = emptyList(),
        val selectedCategory: String? = null,
        val searchQuery: String = "",
        val favoriteProductIds: Set<Int> = emptySet(),
        val total: Int = 0,
        val skip: Int = 0,
        val limit: Int = 10,
        val isLoadingMore: Boolean = false,
        val hasMorePages: Boolean = true
    ) : ProductsUiState

    data class Error(
        val message: String
    ) : ProductsUiState
}
