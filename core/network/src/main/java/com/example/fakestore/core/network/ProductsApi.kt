package com.example.fakestore.core.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApi {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 100,
        @Query("skip") skip: Int = 0
    ): ProductsResponseDto

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): ProductDto

    @GET("products/category-list")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): ProductsResponseDto
}

data class ProductsResponseDto(
    val products: List<ProductDto>,
    val total: Int = 0,
    val skip: Int = 0,
    val limit: Int = 0
)
