package com.example.fakestore.feature.products.profile

import com.example.fakestore.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val username: String = "",
    val user: User? = null,
    val isLoggedIn: Boolean = true,
    val error: String? = null
)
