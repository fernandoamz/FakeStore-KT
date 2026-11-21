package com.example.fakestore.core.security

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "secure_auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cryptoManager: CryptoManager
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("encrypted_access_token")
        private val ACCESS_TOKEN_IV = stringPreferencesKey("access_token_iv")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("encrypted_refresh_token")
        private val REFRESH_TOKEN_IV = stringPreferencesKey("refresh_token_iv")
        private const val KEY_ALIAS = "auth_secret_key"
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        val cipherTextBase64 = preferences[ACCESS_TOKEN_KEY]
        val ivBase64 = preferences[ACCESS_TOKEN_IV]
        if (cipherTextBase64 != null && ivBase64 != null) {
            try {
                val cipherText = Base64.getDecoder().decode(cipherTextBase64)
                val iv = Base64.getDecoder().decode(ivBase64)
                val decryptedBytes = cryptoManager.decrypt(KEY_ALIAS, cipherText, iv)
                String(decryptedBytes, Charsets.UTF_8)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        val (encAccess, accessIv) = cryptoManager.encrypt(KEY_ALIAS, accessToken.toByteArray(Charsets.UTF_8))
        val (encRefresh, refreshIv) = cryptoManager.encrypt(KEY_ALIAS, refreshToken.toByteArray(Charsets.UTF_8))

        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = Base64.getEncoder().encodeToString(encAccess)
            preferences[ACCESS_TOKEN_IV] = Base64.getEncoder().encodeToString(accessIv)
            preferences[REFRESH_TOKEN_KEY] = Base64.getEncoder().encodeToString(encRefresh)
            preferences[REFRESH_TOKEN_IV] = Base64.getEncoder().encodeToString(refreshIv)
        }
    }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
