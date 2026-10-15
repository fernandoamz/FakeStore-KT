package com.example.fakestore.feature.products.auth

data class RegisterUiState(
    val firstname: String = "",
    val lastname: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val phone: String = "",
    val street: String = "",
    val city: String = "",
    val zipcode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val registeredUserId: Int? = null
)
