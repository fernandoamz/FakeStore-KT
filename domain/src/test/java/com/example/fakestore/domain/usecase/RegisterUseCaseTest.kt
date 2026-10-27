package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegisterUseCaseTest {

    private lateinit var authRepository: FakeAuthRepository
    private lateinit var registerUseCase: RegisterUseCase

    @Before
    fun setUp() {
        authRepository = FakeAuthRepository()
        registerUseCase = RegisterUseCase(authRepository)
    }

    @Test
    fun `register with valid data succeeds`() = runTest {
        val result = registerUseCase(
            email = "test@example.com",
            username = "testuser",
            password = "password123",
            firstname = "John",
            lastname = "Doe",
            phone = "123456789",
            street = "Main St",
            city = "City",
            zipcode = "12345"
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `register with short password fails`() = runTest {
        val result = registerUseCase(
            email = "test@example.com",
            username = "testuser",
            password = "123", // too short (< 6 chars)
            firstname = "John",
            lastname = "Doe",
            phone = "123456789",
            street = "Main St",
            city = "City",
            zipcode = "12345"
        )

        assertTrue(result.isFailure)
    }
}

class FakeAuthRepository : AuthRepository {
    override suspend fun login(username: String, password: String): Result<String> {
        return Result.success("fake_token")
    }

    override suspend fun register(
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
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }
        return Result.success(1)
    }
}
