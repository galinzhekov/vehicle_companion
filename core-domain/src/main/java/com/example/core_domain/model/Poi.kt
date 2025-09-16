package com.example.core_domain.model

data class Poi(
    val id: Int,
    val name: String,
    val category: String,
    val rating: Double,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val url: String,
    val isFavorite: Boolean = false
)
