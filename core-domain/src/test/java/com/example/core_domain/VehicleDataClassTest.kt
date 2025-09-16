package com.example.core_domain

import com.example.core_domain.model.Vehicle
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class VehicleDataClassTest {

    @Test
    fun `vehicle creation with required fields is successful`() {
        val name = "My Car"
        val make = "Toyota"
        val model = "Camry"
        val year = 2023
        val id = 123 // Assuming id is part of your Vehicle, can be 0 for new
        val imageUri = "/path/to/image.jpg"
        val vin = "123XYZVIN"
        val fuelType = "Gasoline"

        val vehicle = Vehicle(
            id = id,
            name = name,
            make = make,
            model = model,
            year = year,
            imageUri = imageUri,
            vin = vin,
            fuelType = fuelType
        )

        assertEquals(id, vehicle.id)
        assertEquals(name, vehicle.name)
        assertEquals(make, vehicle.make)
        assertEquals(model, vehicle.model)
        assertEquals(year, vehicle.year)
        assertEquals(imageUri, vehicle.imageUri)
        assertEquals(vin, vehicle.vin)
        assertEquals(fuelType, vehicle.fuelType)
    }

    @Test
    fun `vehicle copy function works as expected`() {
        val initialVehicle = Vehicle(
            id = 1,
            name = "Old Name",
            make = "Honda",
            model = "Civic",
            year = 2020
        )

        val updatedName = "New Name"
        val updatedVehicle = initialVehicle.copy(name = updatedName)

        assertEquals(updatedName, updatedVehicle.name)
        assertEquals(initialVehicle.id, updatedVehicle.id)
        assertEquals(initialVehicle.make, updatedVehicle.make)
    }
}