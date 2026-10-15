package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        username: String,
        password: String,
        firstname: String,
        lastname: String,
        phone: String,
        street: String,
        city: String,
        zipcode: String
    ): Result<Int> {
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(IllegalArgumentException("Por favor ingresa un correo electrónico válido."))
        }
        if (username.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre de usuario no puede estar vacío."))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres."))
        }
        if (firstname.isBlank() || lastname.isBlank()) {
            return Result.failure(IllegalArgumentException("Por favor ingresa tu nombre y apellido."))
        }

        return authRepository.register(
            email = email,
            username = username,
            password = password,
            firstname = firstname,
            lastname = lastname,
            phone = phone,
            street = street,
            city = city,
            zipcode = zipcode
        )
    }
}
