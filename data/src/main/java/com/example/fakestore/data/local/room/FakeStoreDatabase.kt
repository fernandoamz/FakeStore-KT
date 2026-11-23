package com.example.fakestore.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fakestore.data.local.dao.ProductDao
import com.example.fakestore.data.local.dao.MessageDao
import com.example.fakestore.data.local.dao.SearchDao
import com.example.fakestore.data.local.dao.CartDao
import com.example.fakestore.data.local.dao.FavoriteDao
import com.example.fakestore.data.local.entity.ProductEntity
import com.example.fakestore.data.local.entity.MessageEntity
import com.example.fakestore.data.local.entity.MessageOutboxEntity
import com.example.fakestore.data.local.entity.SearchCacheEntity
import com.example.fakestore.data.local.entity.SearchHistoryEntity
import com.example.fakestore.data.local.entity.SearchQueryResultEntity
import com.example.fakestore.data.local.entity.CartEntity
import com.example.fakestore.data.local.entity.CartItemEntity
import com.example.fakestore.data.local.entity.CartOperationEntity
import com.example.fakestore.data.local.entity.FavoriteEntity
import com.example.fakestore.data.local.entity.FavoriteOperationEntity

@Database(
    entities = [
        ProductEntity::class,
        MessageEntity::class,
        MessageOutboxEntity::class,
        SearchHistoryEntity::class,
        SearchCacheEntity::class,
        SearchQueryResultEntity::class,
        CartEntity::class,
        CartItemEntity::class,
        CartOperationEntity::class,
        FavoriteEntity::class,
        FavoriteOperationEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class FakeStoreDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun messageDao(): MessageDao
    abstract fun searchDao(): SearchDao
    abstract fun cartDao(): CartDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val DATABASE_NAME = "fakestore_db"
    }
}
