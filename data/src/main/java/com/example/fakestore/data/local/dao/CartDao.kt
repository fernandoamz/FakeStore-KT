package com.example.fakestore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fakestore.data.local.entity.CartEntity
import com.example.fakestore.data.local.entity.CartItemEntity
import com.example.fakestore.data.local.entity.CartOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM carts WHERE userId = :userId LIMIT 1")
    suspend fun getCartForUser(userId: Int): CartEntity?

    @Query("SELECT * FROM carts WHERE userId = :userId LIMIT 1")
    fun observeCartForUser(userId: Int): Flow<CartEntity?>

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId")
    fun observeCartItems(cartId: String): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCart(cart: CartEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE cartId = :cartId AND productId = :productId")
    suspend fun deleteCartItem(cartId: String, productId: Int)

    @Query("DELETE FROM cart_items WHERE cartId = :cartId")
    suspend fun clearCartItems(cartId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: CartOperationEntity)

    @Query("SELECT * FROM cart_outbox WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPendingOperations(): List<CartOperationEntity>

    @Query("DELETE FROM cart_outbox WHERE id = :operationId")
    suspend fun deleteOperation(operationId: String)
}
