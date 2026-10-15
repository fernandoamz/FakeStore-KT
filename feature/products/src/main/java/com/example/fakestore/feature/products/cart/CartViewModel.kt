package com.example.fakestore.feature.products.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.CartItem
import com.example.fakestore.domain.model.OrderResult
import com.example.fakestore.domain.usecase.cart.AddToCartUseCase
import com.example.fakestore.domain.usecase.cart.CheckoutCartUseCase
import com.example.fakestore.domain.usecase.cart.ClearCartUseCase
import com.example.fakestore.domain.usecase.cart.DecreaseCartQuantityUseCase
import com.example.fakestore.domain.usecase.cart.GetCartItemsUseCase
import com.example.fakestore.domain.usecase.cart.IncreaseCartQuantityUseCase
import com.example.fakestore.domain.usecase.cart.RemoveFromCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class CartViewModel @Inject constructor(
    private val addToCart: AddToCartUseCase,
    private val increaseQuantity: IncreaseCartQuantityUseCase,
    private val decreaseQuantity: DecreaseCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase,
    private val clearCart: ClearCartUseCase,
    private val checkoutCartUseCase: CheckoutCartUseCase,
    getCartItems: GetCartItemsUseCase
) : ViewModel() {

    private val _checkoutState = MutableStateFlow(CheckoutState())

    val uiState: StateFlow<CartUiState> =
        combine(
            getCartItems(),
            _checkoutState
        ) { items, checkout ->
            CartUiState(
                items = items,
                totalItems = items.sumOf { it.quantity },
                totalPrice = items.sumOf { it.subtotal },
                isCheckingOut = checkout.isCheckingOut,
                orderResult = checkout.orderResult,
                checkoutError = checkout.error
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CartUiState()
        )

    fun addProduct(item: CartItem) {
        addToCart(item)
    }

    fun increaseProduct(productId: Int) {
        increaseQuantity(productId)
    }

    fun decreaseProduct(productId: Int) {
        decreaseQuantity(productId)
    }

    fun removeProduct(productId: Int) {
        removeFromCart(productId)
    }

    fun clear() {
        clearCart()
    }

    fun checkout() {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState(isCheckingOut = true)

            val result = checkoutCartUseCase()
            result.onSuccess { orderResult ->
                _checkoutState.value = CheckoutState(
                    isCheckingOut = false,
                    orderResult = orderResult
                )
            }.onFailure { exception ->
                _checkoutState.value = CheckoutState(
                    isCheckingOut = false,
                    error = exception.message ?: "Error al procesar el pedido"
                )
            }
        }
    }

    fun dismissOrderDialog() {
        _checkoutState.value = CheckoutState()
    }

    private data class CheckoutState(
        val isCheckingOut: Boolean = false,
        val orderResult: OrderResult? = null,
        val error: String? = null
    )
}
