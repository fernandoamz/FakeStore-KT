package com.example.fakestore.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class], version = 3, exportSchema = false)
abstract class FakeStoreDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        const val DATABASE_NAME = "fakestore_db"
    }
}
