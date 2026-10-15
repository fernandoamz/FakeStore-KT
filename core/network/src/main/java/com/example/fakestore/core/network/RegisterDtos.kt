package com.example.fakestore.core.network

data class RegisterRequestDto(
    val email: String,
    val username: String,
    val password: String,
    val name: RegisterNameDto,
    val address: RegisterAddressDto,
    val phone: String
)

data class RegisterNameDto(
    val firstname: String,
    val lastname: String
)

data class RegisterAddressDto(
    val city: String,
    val street: String,
    val number: Int = 1,
    val zipcode: String,
    val geolocation: RegisterGeoDto = RegisterGeoDto("-37.3159", "81.1496")
)

data class RegisterGeoDto(
    val lat: String,
    val long: String
)

data class RegisterResponseDto(
    val id: Int
)
