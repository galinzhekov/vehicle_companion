package com.example.vehiclecompanion.di

import android.content.Context
import androidx.room.Room
import com.example.core_data.local.AppDatabase
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
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "vehicle_db").build()

    @Provides fun provideVehicleDao(db: AppDatabase) = db.vehicleDao()
    @Provides fun providePoiDao(db: AppDatabase) = db.poiDao()
}
