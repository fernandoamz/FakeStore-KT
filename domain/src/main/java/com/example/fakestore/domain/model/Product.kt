package com.example.fakestore.domain.model

data class Rating(
    val rate: Double = 0.0,
    val count: Int = 0
)

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: Rating = Rating()
)
