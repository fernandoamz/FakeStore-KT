package com.example.fakestore.core.network

data class CreateCartRequestDto(
    val userId: Int = 1,
    val date: String,
    val products: List<CartProductDto>
)

data class CartProductDto(
    val productId: Int,
    val quantity: Int
)

data class CreateCartResponseDto(
    val id: Int,
    val userId: Int,
    val date: String,
    val products: List<CartProductDto>
)
