package com.example.fakestore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fakestore.data.local.entity.SearchHistoryEntity
import com.example.fakestore.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT 20")
    fun observeRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(history: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearchHistoryById(id: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAllSearchHistory()

    @Query("""
        SELECT p.* FROM products p
        INNER JOIN search_query_results sqr ON p.id = sqr.productId
        INNER JOIN search_cache sc ON sc.id = sqr.searchCacheId
        WHERE sc.normalizedQuery = :normalizedQuery
    """)
    fun observeCachedSearchResults(normalizedQuery: String): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
}
