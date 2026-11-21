package com.example.fakestore.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_outbox",
    foreignKeys = [
        ForeignKey(
            entity = CartEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cartId")]
)
data class CartOperationEntity(
    @PrimaryKey val id: String,
    val cartId: String,
    val operationType: String, // ADD, UPDATE, REMOVE, CLEAR, CHECKOUT
    val payload: String,       // JSON or serialized parameters
    val status: String,        // PENDING, PROCESSING, FAILED
    val createdAt: Long,
    val retryCount: Int
)
