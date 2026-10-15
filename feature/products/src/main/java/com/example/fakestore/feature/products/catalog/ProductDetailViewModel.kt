package com.example.fakestore.feature.products.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.CartItem
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.usecase.GetProductByIdUseCase
import com.example.fakestore.domain.usecase.cart.AddToCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductDetailUiState(
    val isLoading: Boolean = true,
    val product: Product? = null,
    val error: String? = null
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: Int) {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState(isLoading = true)

            try {
                val product = getProductByIdUseCase(productId)

                _uiState.value = ProductDetailUiState(
                    isLoading = false,
                    product = product
                )
            } catch (exception: Exception) {
                _uiState.value = ProductDetailUiState(
                    isLoading = false,
                    error = exception.message ?: "Unable to load product"
                )
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        addToCartUseCase(CartItem(product = product, quantity = quantity))
    }
}
