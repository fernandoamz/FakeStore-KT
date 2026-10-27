package com.example.fakestore.core.network

data class UserDto(
    val id: Int,
    val email: String,
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val phone: String?,
    val address: AddressDto?
)

data class AddressDto(
    val address: String?,
    val city: String?,
    val postalCode: String?
)
