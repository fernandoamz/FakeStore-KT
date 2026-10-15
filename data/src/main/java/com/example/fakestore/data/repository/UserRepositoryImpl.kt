package com.example.fakestore.data.repository

import com.example.fakestore.core.network.UserApi
import com.example.fakestore.domain.model.User
import com.example.fakestore.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {

    override suspend fun getUserProfile(userId: Int): Result<User> {
        return try {
            val dto = userApi.getUserById(userId)
            val user = User(
                id = dto.id,
                email = dto.email,
                username = dto.username,
                firstName = dto.name?.firstname.orEmpty(),
                lastName = dto.name?.lastname.orEmpty(),
                phone = dto.phone.orEmpty(),
                street = dto.address?.street.orEmpty(),
                number = dto.address?.number ?: 0,
                city = dto.address?.city.orEmpty(),
                zipcode = dto.address?.zipcode.orEmpty()
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
