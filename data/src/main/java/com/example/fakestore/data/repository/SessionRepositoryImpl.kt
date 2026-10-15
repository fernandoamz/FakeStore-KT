package com.example.fakestore.data.repository

import com.example.fakestore.data.local.UserSessionManager
import com.example.fakestore.domain.repository.SessionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val sessionManager: UserSessionManager
) : SessionRepository {

    override val isLoggedIn: Flow<Boolean> = sessionManager.isLoggedIn
    override val username: Flow<String?> = sessionManager.username
    override val authToken: Flow<String?> = sessionManager.authToken

    override suspend fun saveSession(token: String, username: String) {
        sessionManager.saveSession(token, username)
    }

    override suspend fun clearSession() {
        sessionManager.clearSession()
    }
}
