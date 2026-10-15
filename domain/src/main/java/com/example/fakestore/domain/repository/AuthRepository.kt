package com.example.fakestore.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<String>
    suspend fun register(
        email: String,
        username: String,
        password: String,
        firstname: String,
        lastname: String,
        phone: String,
        street: String,
        city: String,
        zipcode: String
    ): Result<Int>
}
