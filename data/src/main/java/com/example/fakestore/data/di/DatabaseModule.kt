package com.example.fakestore.data.di

import android.content.Context
import androidx.room.Room
import com.example.fakestore.data.local.room.FakeStoreDatabase
import com.example.fakestore.data.local.dao.ProductDao
import com.example.fakestore.data.local.dao.MessageDao
import com.example.fakestore.data.local.dao.SearchDao
import com.example.fakestore.data.local.dao.CartDao
import com.example.fakestore.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
        .fallbackToDestructiveMigration(true)
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

    @Provides
    @Singleton
    fun provideFavoriteDao(
        database: FakeStoreDatabase
    ): FavoriteDao {
        return database.favoriteDao()
    }
}
