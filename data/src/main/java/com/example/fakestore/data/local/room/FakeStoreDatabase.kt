package com.example.fakestore.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fakestore.data.local.dao.ProductDao
import com.example.fakestore.data.local.entity.ProductEntity

import com.example.fakestore.data.local.entity.MessageEntity
import com.example.fakestore.data.local.entity.MessageOutboxEntity
import com.example.fakestore.data.local.dao.MessageDao
import com.example.fakestore.data.local.entity.SearchCacheEntity
import com.example.fakestore.data.local.entity.SearchHistoryEntity
import com.example.fakestore.data.local.entity.SearchQueryResultEntity
import com.example.fakestore.data.local.dao.SearchDao

@Database(
    entities = [
        ProductEntity::class,
        MessageEntity::class,
        MessageOutboxEntity::class,
        SearchHistoryEntity::class,
        SearchCacheEntity::class,
        SearchQueryResultEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class FakeStoreDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun messageDao(): MessageDao
    abstract fun searchDao(): SearchDao

    companion object {
        const val DATABASE_NAME = "fakestore_db"
    }
}
