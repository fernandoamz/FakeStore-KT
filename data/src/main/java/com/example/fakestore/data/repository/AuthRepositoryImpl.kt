package com.example.fakestore.data.repository

import com.example.fakestore.core.network.AuthApi
import com.example.fakestore.core.network.LoginRequestDto
import com.example.fakestore.core.network.RegisterAddressDto
import com.example.fakestore.core.network.RegisterRequestDto
import com.example.fakestore.core.network.UserApi
import com.example.fakestore.domain.repository.AuthRepository
import com.example.fakestore.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userApi: UserApi,
    private val sessionRepository: SessionRepository
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<String> {
        return try {
            val cleanUsername = username.trim()
            val response = authApi.login(
                LoginRequestDto(
                    username = cleanUsername,
                    password = password.trim()
                )
            )

            val token = response.authToken.ifBlank { "dummy_token_$cleanUsername" }
            sessionRepository.saveSession(token, cleanUsername)
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String,
        firstname: String,
        lastname: String,
        phone: String,
        street: String,
        city: String,
        zipcode: String
    ): Result<Int> {
        return try {
            val response = userApi.registerUser(
                RegisterRequestDto(
                    firstName = firstname.trim(),
                    lastName = lastname.trim(),
                    email = email.trim(),
                    username = username.trim(),
                    password = password.trim(),
                    phone = phone.trim().ifBlank { "+1 202-555-0143" },
                    address = RegisterAddressDto(
                        address = street.trim(),
                        city = city.trim(),
                        postalCode = zipcode.trim()
                    )
                )
            )
            Result.success(response.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
