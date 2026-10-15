package com.example.fakestore.core.network

data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingDto? = null
)

data class RatingDto(
    val rate: Double = 0.0,
    val count: Int = 0
)
