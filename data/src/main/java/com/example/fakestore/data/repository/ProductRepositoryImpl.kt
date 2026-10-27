package com.example.fakestore.data.repository

import androidx.room.withTransaction
import com.example.fakestore.core.network.MetaDto
import com.example.fakestore.core.network.ProductDto
import com.example.fakestore.core.network.ProductsApi
import com.example.fakestore.core.network.ReviewDto
import com.example.fakestore.data.local.room.FakeStoreDatabase
import com.example.fakestore.data.local.room.ProductDao
import com.example.fakestore.data.local.room.ProductEntity
import com.example.fakestore.domain.model.Dimensions
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.model.ProductMeta
import com.example.fakestore.domain.model.Rating
import com.example.fakestore.domain.model.Review
import com.example.fakestore.domain.repository.ProductRepository
import com.example.fakestore.domain.repository.ProductsPageResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        return try {
            val dto = productsApi.getProductById(id)
            val domainProduct = dto.toDomain()
            productDao.insertProducts(listOf(ProductEntity.fromDomain(domainProduct)))
            domainProduct
        } catch (e: Exception) {
            val local = productDao.getProductById(id)
            local?.toDomain() ?: throw e
        }
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
            val response = productsApi.getProducts(limit = 100, skip = 0)
            val entities = response.products.map { dto ->
                ProductEntity.fromDomain(dto.toDomain())
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

    override suspend fun fetchProductsPage(limit: Int, skip: Int): Result<ProductsPageResult> {
        return try {
            val response = productsApi.getProducts(limit = limit, skip = skip)
            val products = response.products.map { it.toDomain() }
            val entities = products.map { ProductEntity.fromDomain(it) }

            database.withTransaction {
                productDao.insertProducts(entities)
            }

            Result.success(
                ProductsPageResult(
                    products = products,
                    total = response.total,
                    skip = response.skip,
                    limit = response.limit
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchProductsByCategory(category: String): Result<ProductsPageResult> {
        return try {
            val response = productsApi.getProductsByCategory(category)
            val products = response.products.map { it.toDomain() }
            val entities = products.map { ProductEntity.fromDomain(it) }

            database.withTransaction {
                productDao.insertProducts(entities)
            }

            Result.success(
                ProductsPageResult(
                    products = products,
                    total = response.total,
                    skip = response.skip,
                    limit = response.limit
                )
            )
        } catch (e: Exception) {
            val cached = productDao.getProductsByCategory(category).first()
                .map { it.toDomain() }
            if (cached.isNotEmpty()) {
                Result.success(
                    ProductsPageResult(
                        products = cached,
                        total = cached.size,
                        skip = 0,
                        limit = cached.size
                    )
                )
            } else {
                Result.failure(e)
            }
        }
    }

    private fun ProductDto.toDomain() =
        Product(
            id = id,
            title = title ?: "",
            price = price ?: 0.0,
            description = description ?: "",
            category = category ?: "",
            image = thumbnail ?: images?.firstOrNull() ?: "",
            rating = Rating(
                rate = rating ?: 0.0,
                count = stock ?: 100
            ),
            discountPercentage = discountPercentage ?: 0.0,
            stock = stock ?: 0,
            brand = brand ?: "",
            images = run {
                val list = mutableListOf<String>()
                val imgs = images
                if (imgs != null) {
                    for (img in imgs) {
                        if (img != null) list.add(img)
                    }
                }
                val thumb = thumbnail
                if (list.isEmpty() && !thumb.isNullOrBlank()) {
                    list.add(thumb)
                }
                list
            },
            tags = tags ?: emptyList(),
            sku = sku ?: "",
            weight = weight ?: 0.0,
            dimensions = Dimensions(
                width = dimensions?.width ?: 0.0,
                height = dimensions?.height ?: 0.0,
                depth = dimensions?.depth ?: 0.0
            ),
            warrantyInformation = warrantyInformation ?: "Garantía oficial de fábrica",
            shippingInformation = shippingInformation ?: "Envío estándar a todo el país",
            returnPolicy = returnPolicy ?: "Devolución sin costo en 30 días",
            availabilityStatus = availabilityStatus ?: "Disponible",
            reviews = reviews?.map {
                Review(
                    rating = it.rating ?: 0.0,
                    comment = it.comment ?: "",
                    date = it.date ?: "",
                    reviewerName = it.reviewerName ?: "Cliente Verificado",
                    reviewerEmail = it.reviewerEmail ?: ""
                )
            } ?: emptyList(),
            minimumOrderQuantity = minimumOrderQuantity ?: 1,
            meta = ProductMeta(
                createdAt = meta?.createdAt ?: "",
                updatedAt = meta?.updatedAt ?: "",
                barcode = meta?.barcode ?: "",
                qrCode = meta?.qrCode ?: ""
            )
        )
}
