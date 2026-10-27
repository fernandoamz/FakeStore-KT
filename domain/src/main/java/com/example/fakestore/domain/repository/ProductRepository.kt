package com.example.fakestore.domain.repository

import com.example.fakestore.domain.model.Product
import kotlinx.coroutines.flow.Flow

data class ProductsPageResult(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductsByCategory(category: String): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product
    suspend fun getCategories(): List<String>
    suspend fun refreshProducts(): Result<Unit>
    suspend fun fetchProductsPage(limit: Int = 10, skip: Int = 0): Result<ProductsPageResult>
}
