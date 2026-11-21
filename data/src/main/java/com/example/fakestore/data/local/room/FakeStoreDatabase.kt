package com.example.fakestore.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

import com.example.fakestore.data.local.entity.MessageEntity
import com.example.fakestore.data.local.entity.MessageOutboxEntity
import com.example.fakestore.data.local.dao.MessageDao

@Database(
    entities = [
        ProductEntity::class,
        MessageEntity::class,
        MessageOutboxEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class FakeStoreDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun messageDao(): MessageDao

    companion object {
        const val DATABASE_NAME = "fakestore_db"
    }
}
