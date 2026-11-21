package com.example.fakestore.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fakestore.data.local.room.FakeStoreDatabase
import com.example.fakestore.data.local.dao.ProductDao
import com.example.fakestore.data.local.dao.MessageDao
import com.example.fakestore.data.local.dao.SearchDao
import com.example.fakestore.data.local.dao.CartDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Creamos la tabla de historial de búsqueda
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `search_history` (
                `id` TEXT NOT NULL,
                `query` TEXT NOT NULL,
                `normalizedQuery` TEXT NOT NULL,
                `searchedAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_search_history_normalizedQuery` ON `search_history` (`normalizedQuery`)")

        // Creamos la tabla de caché de búsqueda
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `search_cache` (
                `id` TEXT NOT NULL,
                `query` TEXT NOT NULL,
                `normalizedQuery` TEXT NOT NULL,
                `cachedAt` INTEGER NOT NULL,
                `expiresAt` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
        """)
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_search_cache_normalizedQuery` ON `search_cache` (`normalizedQuery`)")

        // Creamos la tabla relacional de resultados de búsqueda
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `search_query_results` (
                `searchCacheId` TEXT NOT NULL,
                `productId` INTEGER NOT NULL,
                PRIMARY KEY(`searchCacheId`, `productId`)
            )
        """)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFakeStoreDatabase(
        @ApplicationContext context: Context
    ): FakeStoreDatabase {
        return Room.databaseBuilder(
            context,
            FakeStoreDatabase::class.java,
            FakeStoreDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_4_5)
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(
        database: FakeStoreDatabase
    ): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(
        database: FakeStoreDatabase
    ): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun provideSearchDao(
        database: FakeStoreDatabase
    ): SearchDao {
        return database.searchDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(
        database: FakeStoreDatabase
    ): CartDao {
        return database.cartDao()
    }
}
