package com.example.core_data.repository

import com.example.core_data.local.VehicleDao
import com.example.core_data.local.VehicleEntity
import com.example.core_domain.model.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class VehicleRepositoryImpl @Inject constructor(
    private val dao: VehicleDao
) : VehicleRepository {

    override val vehicles: Flow<List<Vehicle>> =
        dao.getAllVehicles().map { list -> list.map { it.toDomain() } }

    override suspend fun add(vehicle: Vehicle) =
        dao.insert(VehicleEntity.fromDomain(vehicle))

    override suspend fun update(vehicle: Vehicle) =
        dao.update(VehicleEntity.fromDomain(vehicle))

    override suspend fun delete(vehicle: Vehicle) =
        dao.delete(VehicleEntity.fromDomain(vehicle))
}


interface VehicleRepository {
    val vehicles: Flow<List<Vehicle>>
    suspend fun add(vehicle: Vehicle)
    suspend fun update(vehicle: Vehicle)
    suspend fun delete(vehicle: Vehicle)
}