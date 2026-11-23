package com.example.fakestore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fakestore.data.local.entity.FavoriteEntity
import com.example.fakestore.data.local.entity.FavoriteOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT productId FROM favorites")
    fun observeFavoriteProductIds(): Flow<List<Int>>

    @Query("SELECT * FROM favorites")
    suspend fun getAllFavorites(): List<FavoriteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :productId")
    suspend fun deleteFavoriteByProductId(productId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: FavoriteOperationEntity)

    @Query("SELECT * FROM favorite_outbox WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPendingOperations(): List<FavoriteOperationEntity>

    @Query("DELETE FROM favorite_outbox WHERE id = :operationId")
    suspend fun deleteOperation(operationId: String)

    @Query("UPDATE favorite_outbox SET status = :status, retryCount = :retryCount WHERE id = :operationId")
    suspend fun updateOperationStatus(operationId: String, status: String, retryCount: Int)
}
