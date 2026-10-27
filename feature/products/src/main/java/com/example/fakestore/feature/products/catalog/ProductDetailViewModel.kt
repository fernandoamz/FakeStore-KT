package com.example.fakestore.feature.products.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.CartItem
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.repository.FavoritesRepository
import com.example.fakestore.domain.usecase.GetProductByIdUseCase
import com.example.fakestore.domain.usecase.cart.AddToCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    private var currentProductId: Int? = null

    fun loadProduct(productId: Int) {
        if (currentProductId == productId && _uiState.value is ProductDetailUiState.Success) {
            return
        }
        currentProductId = productId
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState.Loading

            try {
                val product = getProductByIdUseCase(productId)

                favoritesRepository.isFavorite(productId).collect { isFav ->
                    val currentQuantity = (_uiState.value as? ProductDetailUiState.Success)?.quantity ?: 1
                    _uiState.value = ProductDetailUiState.Success(
                        product = product,
                        isFavorite = isFav,
                        quantity = currentQuantity
                    )
                }
            } catch (exception: Exception) {
                _uiState.value = ProductDetailUiState.Error(
                    message = exception.message ?: "No se pudo cargar el producto."
                )
            }
        }
    }

    fun retry() {
        currentProductId?.let { loadProduct(it) }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(productId)
        }
    }

    fun increaseQuantity() {
        _uiState.update { state ->
            if (state is ProductDetailUiState.Success) {
                state.copy(quantity = state.quantity + 1)
            } else {
                state
            }
        }
    }

    fun decreaseQuantity() {
        _uiState.update { state ->
            if (state is ProductDetailUiState.Success && state.quantity > 1) {
                state.copy(quantity = state.quantity - 1)
            } else {
                state
            }
        }
    }

    fun addToCart(product: Product, quantity: Int) {
        addToCartUseCase(CartItem(product = product, quantity = quantity))
    }
}
