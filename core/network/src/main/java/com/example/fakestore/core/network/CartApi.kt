package com.example.fakestore.core.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CartApi {

    @POST("carts/add")
    suspend fun createCart(
        @Body request: CreateCartRequestDto
    ): CreateCartResponseDto

    @GET("carts/user/{userId}")
    suspend fun getUserCarts(
        @Path("userId") userId: Int
    ): UserCartsResponseDto

    @PUT("carts/{id}")
    suspend fun updateCart(
        @Path("id") id: Int,
        @Body request: UpdateCartRequestDto
    ): CreateCartResponseDto

    @DELETE("carts/{id}")
    suspend fun deleteCart(
        @Path("id") id: Int
    ): DeleteCartResponseDto
}

data class UserCartsResponseDto(
    val carts: List<CreateCartResponseDto>,
    val total: Int = 0,
    val skip: Int = 0,
    val limit: Int = 0
)

data class UpdateCartRequestDto(
    val merge: Boolean = true,
    val products: List<CartProductDto>
)

data class DeleteCartResponseDto(
    val id: Int,
    val deleted: Boolean,
    val deletedOn: String? = null
)
