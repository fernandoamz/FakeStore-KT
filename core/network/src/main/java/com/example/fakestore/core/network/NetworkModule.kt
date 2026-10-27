package com.example.fakestore.core.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideProductsApi(
        retrofit: Retrofit
    ): ProductsApi {
        return retrofit.create(ProductsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCartApi(
        retrofit: Retrofit
    ): CartApi {
        return retrofit.create(CartApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(
        retrofit: Retrofit
    ): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit
    ): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    @AssistantRetrofit
    fun provideAssistantRetrofit(
        @AssistantBaseUrl baseUrl: String
    ): Retrofit {
        val normalized = when {
            baseUrl.isBlank() -> "https://example.invalid/"
            baseUrl.endsWith("/") -> baseUrl
            else -> "$baseUrl/"
        }
        return Retrofit.Builder()
            .baseUrl(normalized)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAssistantApi(
        @AssistantRetrofit retrofit: Retrofit
    ): AssistantApi {
        return retrofit.create(AssistantApi::class.java)
    }
}
