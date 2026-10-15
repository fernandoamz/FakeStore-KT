package com.example.fakestore.domain.repository

import com.example.fakestore.domain.model.CartItem
import com.example.fakestore.domain.model.OrderResult
import com.example.fakestore.domain.model.Product
import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val cartItems: StateFlow<List<CartItem>>
    fun addProduct(product: Product, quantity: Int = 1)
    fun addProduct(item: CartItem)
    fun increaseQuantity(productId: Int)
    fun decreaseQuantity(productId: Int)
    fun removeProduct(productId: Int)
    fun updateQuantity(productId: Int, quantity: Int)
    fun clearCart()
    suspend fun checkout(): Result<OrderResult>
}
