package com.example.fakestore.domain.model

data class OrderResult(
    val id: Int,
    val date: String,
    val totalItems: Int,
    val totalPrice: Double
)
