package com.example.fakestore.data.repository

import com.example.fakestore.core.network.CartApi
import com.example.fakestore.core.network.CartProductDto
import com.example.fakestore.core.network.CreateCartRequestDto
import com.example.fakestore.domain.model.CartItem
import com.example.fakestore.domain.model.OrderResult
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.repository.CartRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartApi: CartApi
) : CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    override val cartItems: StateFlow<List<CartItem>> =
        _cartItems.asStateFlow()

    override fun addProduct(product: Product, quantity: Int) {
        addProduct(CartItem(product = product, quantity = quantity))
    }

    override fun addProduct(item: CartItem) {
        _cartItems.update { currentItems ->
            val existingItem = currentItems.find {
                it.product.id == item.product.id
            }

            if (existingItem != null) {
                currentItems.map {
                    if (it.product.id == item.product.id) {
                        it.copy(quantity = it.quantity + item.quantity)
                    } else {
                        it
                    }
                }
            } else {
                currentItems + item
            }
        }
    }

    override fun increaseQuantity(productId: Int) {
        _cartItems.update { items ->
            items.map { item ->
                if (item.product.id == productId) {
                    item.copy(quantity = item.quantity + 1)
                } else {
                    item
                }
            }
        }
    }

    override fun decreaseQuantity(productId: Int) {
        _cartItems.update { items ->
            items.mapNotNull { item ->
                if (item.product.id != productId) {
                    item
                } else if (item.quantity > 1) {
                    item.copy(quantity = item.quantity - 1)
                } else {
                    null
                }
            }
        }
    }

    override fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeProduct(productId)
            return
        }
        _cartItems.update { items ->
            items.map { item ->
                if (item.product.id == productId) {
                    item.copy(quantity = quantity)
                } else {
                    item
                }
            }
        }
    }

    override fun removeProduct(productId: Int) {
        _cartItems.update { items ->
            items.filterNot { it.product.id == productId }
        }
    }

    override fun clearCart() {
        _cartItems.value = emptyList()
    }

    override suspend fun checkout(): Result<OrderResult> {
        val currentList = _cartItems.value
        if (currentList.isEmpty()) {
            return Result.failure(IllegalStateException("Cart is empty"))
        }

        val totalItems = currentList.sumOf { it.quantity }
        val totalPrice = currentList.sumOf { it.subtotal }
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return try {
            val response = cartApi.createCart(
                CreateCartRequestDto(
                    userId = 1,
                    products = currentList.map {
                        CartProductDto(id = it.product.id, quantity = it.quantity)
                    }
                )
            )

            clearCart()

            Result.success(
                OrderResult(
                    id = response.id,
                    date = dateString,
                    totalItems = totalItems,
                    totalPrice = totalPrice
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
