package com.example.core_data.network

import com.example.core_data.local.PoiEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface PoiApiService {
    @GET("api/v2/pois/discover")
    suspend fun discoverPois(
        @Query("sw_corner") swCorner: String,
        @Query("ne_corner") neCorner: String,
        @Query("page_size") pageSize: Int = 50
    ): PoiResponse
}

data class PoiResponse(val pois: List<PoiNetwork>)

data class PoiNetwork(
    val id: Int,
    val name: String,
    val url: String,
    val primary_category_display_name: String?,
    val rating: Double?,
    val v_320x320_url: String?,
    val loc: List<Double>
) {
    fun toEntity(): PoiEntity {
        val lat = loc.getOrNull(1) ?: 0.0
        val lon = loc.getOrNull(0) ?: 0.0
        return PoiEntity(
            id = id,
            name = name,
            category = primary_category_display_name ?: "Other",
            rating = rating ?: 0.0,
            imageUrl = v_320x320_url ?: "",
            latitude = lat,
            longitude = lon,
            url = url,
            isFavorite = false
        )
    }
}