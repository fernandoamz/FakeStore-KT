package com.example.fakestore.core.network

data class RegisterRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String,
    val password: String,
    val phone: String,
    val address: RegisterAddressDto? = null
)

data class RegisterAddressDto(
    val address: String,
    val city: String,
    val postalCode: String
)

data class RegisterResponseDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val username: String
)
