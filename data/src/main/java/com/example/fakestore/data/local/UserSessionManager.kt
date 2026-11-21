package com.example.fakestore.data.local

import com.example.fakestore.core.security.TokenManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class UserSessionManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    val authToken: Flow<String?> = tokenManager.accessToken

    val username: Flow<String?> = tokenManager.accessToken.map { token ->
        if (token != null) "User" else null
    }

    val isLoggedIn: Flow<Boolean> = tokenManager.accessToken.map { token ->
        !token.isNullOrBlank()
    }

    suspend fun saveSession(token: String, username: String) {
        tokenManager.saveTokens(accessToken = token, refreshToken = token)
    }

    suspend fun clearSession() {
        tokenManager.clearTokens()
    }
}
