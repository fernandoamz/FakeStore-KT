package com.example.fakestore.feature.products.checkout

import com.example.fakestore.domain.model.CartItem

data class CheckoutUiState(
    val isLoadingUser: Boolean = false,
    val isSubmittingOrder: Boolean = false,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val street: String = "",
    val city: String = "",
    val zipcode: String = "",
    val cardNumber: String = "4532 •••• •••• 8892",
    val expiryDate: String = "08/28",
    val cvv: String = "882",
    val selectedPaymentMethod: String = "Tarjeta de Crédito",
    val cartItems: List<CartItem> = emptyList(),
    val totalItems: Int = 0,
    val totalPrice: Double = 0.0,
    val error: String? = null
) {
    val shippingFee: Double = if (totalPrice > 0) 0.0 else 0.0
    val finalTotal: Double = totalPrice + shippingFee
}
