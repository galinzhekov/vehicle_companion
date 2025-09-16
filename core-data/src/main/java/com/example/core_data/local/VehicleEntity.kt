package com.example.core_data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core_domain.model.Vehicle

@Entity(tableName = "vehicle_table")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val make: String,
    val model: String,
    val year: Int,
    val vin: String,
    val fuelType: String,
    val imageUri: String? = null
) {
    fun toDomain() = Vehicle(id, name, make, model, year, vin, fuelType, imageUri)
    companion object {
        fun fromDomain(vehicle: Vehicle) =
            VehicleEntity(vehicle.id, vehicle.name, vehicle.make, vehicle.model, vehicle.year, vehicle.vin, vehicle.fuelType, vehicle.imageUri)
    }
}
