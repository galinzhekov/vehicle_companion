package com.example.vehiclecompanion.di

import com.example.core_data.network.PoiApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api2.roadtrippers.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun providePoiApiService(retrofit: Retrofit) = retrofit.create(PoiApiService::class.java)
}
