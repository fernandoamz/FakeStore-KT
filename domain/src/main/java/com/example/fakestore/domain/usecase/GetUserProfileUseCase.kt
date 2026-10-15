package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.model.User
import com.example.fakestore.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Int = 1): Result<User> =
        userRepository.getUserProfile(userId)
}
