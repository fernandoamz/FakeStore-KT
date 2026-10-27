package com.example.fakestore.di

import com.example.fakestore.BuildConfig
import com.example.fakestore.core.network.AssistantBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AssistantConfigModule {

    @Provides
    @Singleton
    @AssistantBaseUrl
    fun provideAssistantBaseUrl(): String = BuildConfig.ASSISTANT_BASE_URL
}
