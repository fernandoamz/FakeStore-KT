package com.example.fakestore.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "search_cache",
    indices = [Index("normalizedQuery", unique = true)]
)
data class SearchCacheEntity(
    @PrimaryKey val id: String,
    val query: String,
    val normalizedQuery: String,
    val cachedAt: Long,
    val expiresAt: Long
)
