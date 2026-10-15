package com.example.fakestore.domain.repository

import com.example.fakestore.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<List<Product>>
    fun getProductsByCategory(category: String): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product
    suspend fun getCategories(): List<String>
    suspend fun refreshProducts(): Result<Unit>
}
