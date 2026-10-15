package com.example.fakestore.domain.repository

import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val isLoggedIn: Flow<Boolean>
    val username: Flow<String?>
    val authToken: Flow<String?>
    suspend fun saveSession(token: String, username: String)
    suspend fun clearSession()
}
