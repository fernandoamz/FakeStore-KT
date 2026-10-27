package com.example.fakestore.data.repository

import com.example.fakestore.core.network.UserApi
import com.example.fakestore.core.network.UserDto
import com.example.fakestore.domain.model.User
import com.example.fakestore.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getUserProfile(userId: Int): Result<User> {
        return try {
            val dto = userApi.getUserById(userId)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun UserDto.toDomain() = User(
        id = id,
        email = email,
        username = username,
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        phone = phone ?: "",
        street = address?.address ?: "",
        number = 0,
        city = address?.city ?: "",
        zipcode = address?.postalCode ?: ""
    )
}
