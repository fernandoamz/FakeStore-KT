package com.example.fakestore.domain.model

data class SearchHistory(
    val id: String,
    val query: String,
    val searchedAt: Long
)
