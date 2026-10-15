package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.repository.SessionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class CheckSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    val isLoggedIn: Flow<Boolean> = sessionRepository.isLoggedIn
    val username: Flow<String?> = sessionRepository.username
}
