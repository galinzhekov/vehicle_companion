package com.example.core_data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core_domain.model.Poi

@Entity(tableName = "poi_table")
data class PoiEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val category: String,
    val rating: Double,
    val imageUrl: String,
    val latitude: Double,
    val longitude: Double,
    val url: String,
    val isFavorite: Boolean = false
) {
    fun toDomain() = Poi(id, name, category, rating, imageUrl, latitude, longitude, url, isFavorite)
    companion object {
        fun fromDomain(poi: Poi) =
            PoiEntity(poi.id, poi.name, poi.category, poi.rating, poi.imageUrl, poi.latitude, poi.longitude, poi.url, poi.isFavorite)
    }
}
