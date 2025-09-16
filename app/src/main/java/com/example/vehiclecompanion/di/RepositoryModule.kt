package com.example.vehiclecompanion.di

import com.example.core_data.repository.PoiRepository
import com.example.core_data.repository.PoiRepositoryImpl
import com.example.core_data.repository.VehicleRepository
import com.example.core_data.repository.VehicleRepositoryImpl
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
    abstract fun bindVehicleRepository(
        impl: VehicleRepositoryImpl
    ): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindPoiRepository(
        impl: PoiRepositoryImpl
    ): PoiRepository
}
