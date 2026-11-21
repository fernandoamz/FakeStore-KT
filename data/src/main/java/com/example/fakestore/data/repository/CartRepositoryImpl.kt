package com.example.fakestore.data.repository

import androidx.room.withTransaction
import com.example.fakestore.core.network.CartApi
import com.example.fakestore.core.network.CartProductDto
import com.example.fakestore.core.network.CreateCartRequestDto
import com.example.fakestore.data.local.room.FakeStoreDatabase
import com.example.fakestore.data.local.dao.CartDao
import com.example.fakestore.data.local.entity.CartEntity
import com.example.fakestore.data.local.entity.CartItemEntity
import com.example.fakestore.data.local.entity.CartOperationEntity
import com.example.fakestore.domain.model.CartItem
import com.example.fakestore.domain.model.OrderResult
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.repository.CartRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val database: FakeStoreDatabase,
    private val cartDao: CartDao,
    private val cartApi: CartApi
) : CartRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val defaultUserId = 1
    private val defaultCartId = "cart_$defaultUserId"

    override val cartItems: StateFlow<List<CartItem>> =
        cartDao.observeCartItems(defaultCartId)
            .map { entities ->
                entities.map { entity ->
                    CartItem(
                        product = Product(
                            id = entity.productId,
                            title = entity.title,
                            price = entity.price,
                            description = "",
                            category = "",
                            image = entity.image,
                            rating = com.example.fakestore.domain.model.Rating(0.0, 0)
                        ),
                        quantity = entity.quantity
                    )
                }
            }
            .stateIn(
                scope = repositoryScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList()
            )

    init {
        repositoryScope.launch {
            if (cartDao.getCartForUser(defaultUserId) == null) {
                cartDao.insertCart(
                    CartEntity(id = defaultCartId, userId = defaultUserId, updatedAt = System.currentTimeMillis())
                )
            }
        }
    }

    override fun addProduct(product: Product, quantity: Int) {
        addProduct(CartItem(product = product, quantity = quantity))
    }

    override fun addProduct(item: CartItem) {
        repositoryScope.launch {
            database.withTransaction {
                val itemId = "${defaultCartId}_${item.product.id}"
                val itemEntity = CartItemEntity(
                    id = itemId,
                    cartId = defaultCartId,
                    productId = item.product.id,
                    title = item.product.title,
                    price = item.product.price,
                    image = item.product.image,
                    quantity = item.quantity
                )
                cartDao.insertCartItem(itemEntity)
                cartDao.insertOperation(
                    CartOperationEntity(
                        id = UUID.randomUUID().toString(),
                        cartId = defaultCartId,
                        operationType = "ADD",
                        payload = "{\"productId\":${item.product.id},\"quantity\":${item.quantity}}",
                        status = "PENDING",
                        createdAt = System.currentTimeMillis(),
                        retryCount = 0
                    )
                )
            }
        }
    }

    override fun increaseQuantity(productId: Int) {
        repositoryScope.launch {
            database.withTransaction {
                cartDao.insertOperation(
                    CartOperationEntity(
                        id = UUID.randomUUID().toString(),
                        cartId = defaultCartId,
                        operationType = "INCREASE",
                        payload = "{\"productId\":$productId}",
                        status = "PENDING",
                        createdAt = System.currentTimeMillis(),
                        retryCount = 0
                    )
                )
            }
        }
    }

    override fun decreaseQuantity(productId: Int) {
        repositoryScope.launch {
            database.withTransaction {
                cartDao.insertOperation(
                    CartOperationEntity(
                        id = UUID.randomUUID().toString(),
                        cartId = defaultCartId,
                        operationType = "DECREASE",
                        payload = "{\"productId\":$productId}",
                        status = "PENDING",
                        createdAt = System.currentTimeMillis(),
                        retryCount = 0
                    )
                )
            }
        }
    }

    override fun updateQuantity(productId: Int, quantity: Int) {
        repositoryScope.launch {
            database.withTransaction {
                cartDao.insertOperation(
                    CartOperationEntity(
                        id = UUID.randomUUID().toString(),
                        cartId = defaultCartId,
                        operationType = "UPDATE",
                        payload = "{\"productId\":$productId,\"quantity\":$quantity}",
                        status = "PENDING",
                        createdAt = System.currentTimeMillis(),
                        retryCount = 0
                    )
                )
            }
        }
    }

    override fun removeProduct(productId: Int) {
        repositoryScope.launch {
            database.withTransaction {
                cartDao.deleteCartItem(defaultCartId, productId)
                cartDao.insertOperation(
                    CartOperationEntity(
                        id = UUID.randomUUID().toString(),
                        cartId = defaultCartId,
                        operationType = "REMOVE",
                        payload = "{\"productId\":$productId}",
                        status = "PENDING",
                        createdAt = System.currentTimeMillis(),
                        retryCount = 0
                    )
                )
            }
        }
    }

    override fun clearCart() {
        repositoryScope.launch {
            database.withTransaction {
                cartDao.clearCartItems(defaultCartId)
                cartDao.insertOperation(
                    CartOperationEntity(
                        id = UUID.randomUUID().toString(),
                        cartId = defaultCartId,
                        operationType = "CLEAR",
                        payload = "{}",
                        status = "PENDING",
                        createdAt = System.currentTimeMillis(),
                        retryCount = 0
                    )
                )
            }
        }
    }

    override suspend fun checkout(): Result<OrderResult> {
        return try {
            val response = cartApi.createCart(
                CreateCartRequestDto(
                    userId = defaultUserId,
                    products = emptyList()
                )
            )
            Result.success(OrderResult(response.id, "2026-03-05", 1, 0.0))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
