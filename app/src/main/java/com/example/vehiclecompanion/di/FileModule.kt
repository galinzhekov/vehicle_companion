package com.example.vehiclecompanion.di

import com.example.core_data.local.FileManager
import com.example.core_data.local.FileManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FileModule {
    @Provides
    @Singleton
    fun provideFileManager(
        impl: FileManagerImpl
    ): FileManager = impl
}
