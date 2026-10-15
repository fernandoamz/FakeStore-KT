package com.example.fakestore.feature.products.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.OrderResult
import com.example.fakestore.domain.usecase.GetUserProfileUseCase
import com.example.fakestore.domain.usecase.cart.CheckoutCartUseCase
import com.example.fakestore.domain.usecase.cart.GetCartItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val checkoutCartUseCase: CheckoutCartUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUser = true) }

            launch {
                getCartItemsUseCase().collect { items ->
                    _uiState.update { state ->
                        state.copy(
                            cartItems = items,
                            totalItems = items.sumOf { it.quantity },
                            totalPrice = items.sumOf { it.subtotal }
                        )
                    }
                }
            }

            val userResult = getUserProfileUseCase(userId = 1)
            userResult.onSuccess { user ->
                _uiState.update { state ->
                    state.copy(
                        isLoadingUser = false,
                        name = user.fullName.ifBlank { "John Doe" },
                        email = user.email.ifBlank { "john@gmail.com" },
                        phone = user.phone.ifBlank { "1-570-236-7033" },
                        street = if (user.street.isNotBlank()) "${user.street} N° ${user.number}" else "7535 New Road N° 3",
                        city = user.city.ifBlank { "Kilback" },
                        zipcode = user.zipcode.ifBlank { "12926-3874" }
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(
                        isLoadingUser = false,
                        name = "John Doe",
                        email = "john@gmail.com",
                        phone = "1-570-236-7033",
                        street = "7535 New Road N° 3",
                        city = "Kilback",
                        zipcode = "12926-3874"
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) { _uiState.update { it.copy(name = value) } }
    fun onEmailChange(value: String) { _uiState.update { it.copy(email = value) } }
    fun onPhoneChange(value: String) { _uiState.update { it.copy(phone = value) } }
    fun onStreetChange(value: String) { _uiState.update { it.copy(street = value) } }
    fun onCityChange(value: String) { _uiState.update { it.copy(city = value) } }
    fun onZipcodeChange(value: String) { _uiState.update { it.copy(zipcode = value) } }
    fun onPaymentMethodChange(value: String) { _uiState.update { it.copy(selectedPaymentMethod = value) } }

    fun submitOrder(onSuccess: (OrderResult) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingOrder = true, error = null) }

            val result = checkoutCartUseCase()
            result.onSuccess { orderResult ->
                _uiState.update { it.copy(isSubmittingOrder = false) }
                onSuccess(orderResult)
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isSubmittingOrder = false,
                        error = exception.message ?: "Error al procesar la orden"
                    )
                }
            }
        }
    }
}
