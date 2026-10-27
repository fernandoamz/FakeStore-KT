package com.example.fakestore.feature.products.catalog

import com.example.fakestore.domain.model.Product

sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState
    data class Success(
        val product: Product,
        val isFavorite: Boolean = false,
        val quantity: Int = 1
    ) : ProductDetailUiState
    data class Error(val message: String) : ProductDetailUiState
}
