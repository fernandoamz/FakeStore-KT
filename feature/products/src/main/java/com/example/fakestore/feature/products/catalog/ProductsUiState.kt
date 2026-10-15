package com.example.fakestore.feature.products.catalog

import com.example.fakestore.domain.model.Product

data class ProductsUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val favoriteProductIds: Set<Int> = emptySet(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)
