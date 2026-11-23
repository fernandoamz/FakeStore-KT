package com.example.fakestore.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    indices = [Index(value = ["productId"], unique = true)]
)
data class FavoriteEntity(
    @PrimaryKey var id: String,
    val productId: Int,
    val addedAt: Long
)

@Entity(
    tableName = "favorite_outbox",
    indices = [Index("productId")]
)
data class FavoriteOperationEntity(
    @PrimaryKey var id: String,
    val productId: Int,
    val action: String,
    val status: String,
    val createdAt: Long,
    val retryCount: Int
)
