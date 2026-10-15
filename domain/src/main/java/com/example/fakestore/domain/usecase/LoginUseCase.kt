package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<String> {
        if (username.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre de usuario no puede estar vacío."))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("La contraseña no puede estar vacía."))
        }
        return authRepository.login(username, password)
    }
}
