package com.example.core_domain.model

data class Vehicle(
    val id: Int = 0,
    val name: String,
    val make: String,
    val model: String,
    val year: Int,
    val vin: String = "",
    val fuelType: String = "",
    val imageUri: String? = null
)