package com.example.fakestore.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "search_query_results",
    primaryKeys = ["searchCacheId", "productId"]
)
data class SearchQueryResultEntity(
    val searchCacheId: String,
    val productId: Int
)
