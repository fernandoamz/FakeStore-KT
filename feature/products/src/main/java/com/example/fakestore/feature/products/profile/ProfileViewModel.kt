package com.example.fakestore.feature.products.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.usecase.CheckSessionUseCase
import com.example.fakestore.domain.usecase.GetUserProfileUseCase
import com.example.fakestore.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        observeSession()
        loadUserProfile()
    }

    private fun observeSession() {
        viewModelScope.launch {
            checkSessionUseCase.isLoggedIn.collect { loggedIn ->
                _uiState.update { it.copy(isLoggedIn = loggedIn) }
            }
        }
        viewModelScope.launch {
            checkSessionUseCase.username.collect { username ->
                if (!username.isNullOrBlank()) {
                    _uiState.update { it.copy(username = username) }
                }
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = getUserProfileUseCase(userId = 1)
            result.onSuccess { user ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        user = user,
                        username = user.username.ifBlank { state.username.ifBlank { "johnd" } }
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar perfil"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
