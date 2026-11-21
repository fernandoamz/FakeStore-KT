package com.example.fakestore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carts")
data class CartEntity(
    @PrimaryKey val id: String,
    val userId: Int,
    val updatedAt: Long
)