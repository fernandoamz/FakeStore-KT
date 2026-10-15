package com.example.fakestore.core.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {

    @GET("users/{id}")
    suspend fun getUserById(
        @Path("id") id: Int
    ): UserDto

    @POST("users")
    suspend fun registerUser(
        @Body request: RegisterRequestDto
    ): RegisterResponseDto
}
