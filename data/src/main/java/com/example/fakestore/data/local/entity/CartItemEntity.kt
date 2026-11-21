package com.example.fakestore.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = CartEntity::class,
            parentColumns = ["id"],
            childColumns = ["cartId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cartId"), Index("productId")]
)
data class CartItemEntity(
    @PrimaryKey val id: String,
    val cartId: String,
    val productId: Int,
    val title: String,
    val price: Double,
    val image: String,
    val quantity: Int
)
