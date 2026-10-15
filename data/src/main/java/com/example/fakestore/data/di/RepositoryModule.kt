package com.example.fakestore.data.di

import com.example.fakestore.data.repository.AuthRepositoryImpl
import com.example.fakestore.data.repository.CartRepositoryImpl
import com.example.fakestore.data.repository.ProductRepositoryImpl
import com.example.fakestore.data.repository.SessionRepositoryImpl
import com.example.fakestore.data.repository.UserRepositoryImpl
import com.example.fakestore.domain.repository.AuthRepository
import com.example.fakestore.domain.repository.CartRepository
import com.example.fakestore.domain.repository.ProductRepository
import com.example.fakestore.domain.repository.SessionRepository
import com.example.fakestore.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        implementation: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        implementation: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        implementation: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        implementation: SessionRepositoryImpl
    ): SessionRepository
}
