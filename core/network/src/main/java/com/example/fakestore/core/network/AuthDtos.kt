package com.example.fakestore.core.network

data class LoginRequestDto(
    val username: String,
    val password: String,
    val expiresInMins: Int = 30
)

data class LoginResponseDto(
    val id: Int? = null,
    val username: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val token: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
) {
    val authToken: String
        get() = accessToken ?: token ?: refreshToken ?: ""
}
