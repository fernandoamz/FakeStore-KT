package com.example.fakestore.data.repository

import androidx.room.withTransaction
import com.example.fakestore.core.network.ProductDto
import com.example.fakestore.core.network.ProductsApi
import com.example.fakestore.data.local.room.FakeStoreDatabase
import com.example.fakestore.data.local.room.ProductDao
import com.example.fakestore.data.local.room.ProductEntity
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.model.Rating
import com.example.fakestore.domain.repository.ProductRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl @Inject constructor(
    private val productsApi: ProductsApi,
    private val productDao: ProductDao,
    private val database: FakeStoreDatabase
) : ProductRepository {

    override fun getProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getProductsByCategory(category: String): Flow<List<Product>> {
        return productDao.getProductsByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getProductById(id: Int): Product {
        val local = productDao.getProductById(id)
        if (local != null) {
            return local.toDomain()
        }
        return productsApi.getProductById(id).toDomain()
    }

    override suspend fun getCategories(): List<String> {
        return try {
            productsApi.getCategories()
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun refreshProducts(): Result<Unit> {
        return try {
            val remoteDtos = productsApi.getProducts()
            val entities = remoteDtos.map { dto ->
                ProductEntity(
                    id = dto.id,
                    title = dto.title,
                    price = dto.price,
                    description = dto.description,
                    category = dto.category,
                    image = dto.image,
                    ratingRate = dto.rating?.rate ?: 0.0,
                    ratingCount = dto.rating?.count ?: 0
                )
            }
            database.withTransaction {
                productDao.clearAllProducts()
                productDao.insertProducts(entities)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun ProductDto.toDomain() =
        Product(
            id = id,
            title = title,
            price = price,
            description = description,
            category = category,
            image = image,
            rating = Rating(
                rate = rating?.rate ?: 0.0,
                count = rating?.count ?: 0
            )
        )
}
