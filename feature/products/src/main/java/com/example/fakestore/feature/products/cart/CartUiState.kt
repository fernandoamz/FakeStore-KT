package com.example.fakestore.feature.products.cart

import com.example.fakestore.domain.model.CartItem
import com.example.fakestore.domain.model.OrderResult

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val totalItems: Int = 0,
    val totalPrice: Double = 0.0,
    val isCheckingOut: Boolean = false,
    val orderResult: OrderResult? = null,
    val checkoutError: String? = null
)
