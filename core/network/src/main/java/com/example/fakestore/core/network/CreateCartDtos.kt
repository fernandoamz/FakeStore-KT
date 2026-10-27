package com.example.fakestore.core.network

data class CreateCartRequestDto(
    val userId: Int = 1,
    val products: List<CartProductDto>
)

data class CartProductDto(
    val id: Int,
    val quantity: Int
)

data class CreateCartResponseDto(
    val id: Int,
    val userId: Int,
    val products: List<CartProductResponseProductDto>,
    val total: Double,
    val totalProducts: Int,
    val totalQuantity: Int
)

data class CartProductResponseProductDto(
    val id: Int,
    val title: String? = null,
    val price: Double,
    val quantity: Int,
    val total: Double
)
