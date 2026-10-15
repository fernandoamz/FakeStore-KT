package com.example.fakestore.feature.products.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFirstnameChange(value: String) { _uiState.update { it.copy(firstname = value, errorMessage = null) } }
    fun onLastnameChange(value: String) { _uiState.update { it.copy(lastname = value, errorMessage = null) } }
    fun onUsernameChange(value: String) { _uiState.update { it.copy(username = value, errorMessage = null) } }
    fun onEmailChange(value: String) { _uiState.update { it.copy(email = value, errorMessage = null) } }
    fun onPasswordChange(value: String) { _uiState.update { it.copy(password = value, errorMessage = null) } }
    fun togglePasswordVisibility() { _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }
    fun onPhoneChange(value: String) { _uiState.update { it.copy(phone = value, errorMessage = null) } }
    fun onStreetChange(value: String) { _uiState.update { it.copy(street = value, errorMessage = null) } }
    fun onCityChange(value: String) { _uiState.update { it.copy(city = value, errorMessage = null) } }
    fun onZipcodeChange(value: String) { _uiState.update { it.copy(zipcode = value, errorMessage = null) } }

    fun register() {
        val current = _uiState.value

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = registerUseCase(
                email = current.email,
                username = current.username,
                password = current.password,
                firstname = current.firstname,
                lastname = current.lastname,
                phone = current.phone,
                street = current.street,
                city = current.city,
                zipcode = current.zipcode
            )

            result.onSuccess { userId ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        registeredUserId = userId
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al registrar usuario."
                    )
                }
            }
        }
    }
}
