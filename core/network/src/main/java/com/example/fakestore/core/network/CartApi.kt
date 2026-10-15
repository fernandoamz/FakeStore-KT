package com.example.fakestore.core.network

import retrofit2.http.Body
import retrofit2.http.POST

interface CartApi {

    @POST("carts")
    suspend fun createCart(
        @Body request: CreateCartRequestDto
    ): CreateCartResponseDto
}
