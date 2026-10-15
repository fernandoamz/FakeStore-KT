package com.example.fakestore.domain.repository

import com.example.fakestore.domain.model.User

interface UserRepository {
    suspend fun getUserProfile(userId: Int = 1): Result<User>
}
