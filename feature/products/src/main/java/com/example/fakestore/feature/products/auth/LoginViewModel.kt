package com.example.fakestore.feature.products.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun useTestCredentials() {
        _uiState.update {
            it.copy(
                username = "johnd",
                password = "m38rmF$",
                errorMessage = null
            )
        }
    }

    fun login() {
        val current = _uiState.value
        if (current.username.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingresa tu nombre de usuario.") }
            return
        }
        if (current.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor ingresa tu contraseña.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = loginUseCase(
                username = current.username,
                password = current.password
            )

            result.onSuccess { token ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        token = token
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al iniciar sesión. Verifica tus credenciales."
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
