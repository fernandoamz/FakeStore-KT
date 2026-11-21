package com.example.fakestore.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "search_history",
    indices = [Index("normalizedQuery", unique = true)]
)
data class SearchHistoryEntity(
    @PrimaryKey val id: String,
    val query: String,
    val normalizedQuery: String,
    val searchedAt: Long
)
