package com.example.core_data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VehicleEntity::class, PoiEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun poiDao(): PoiDao
}
