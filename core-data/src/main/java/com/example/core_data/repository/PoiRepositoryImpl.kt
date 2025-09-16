package com.example.core_data.repository

import com.example.core_data.local.PoiDao
import com.example.core_data.local.PoiEntity
import com.example.core_data.network.PoiApiService
import com.example.core_domain.model.Poi
import com.example.core_domain.util.safeCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoiRepositoryImpl @Inject constructor(
    private val api: PoiApiService,
    private val dao: PoiDao
): PoiRepository  {
    override fun pois(): Flow<List<Poi>> = dao.getAll().map { it.map { e -> e.toDomain() } }
    override fun favorites(): Flow<List<Poi>> = dao.getFavorites().map { it.map { e -> e.toDomain() } }

    override suspend fun fetchPois(
        swCorner: String,
        neCorner: String,
        success: () -> Unit,
        error: (Throwable) -> Unit
    ) {
        safeCall(
            block = {
                val resp = api.discoverPois(swCorner, neCorner)
                val entities = resp.pois.map { apiPoi ->
                    val existing = dao.getById(apiPoi.id)
                    val isFavorite = existing?.isFavorite ?: false
                    apiPoi.toEntity().copy(isFavorite = isFavorite)
                }
                dao.insertAll(entities)
            },
            success = { success() },
            error = { error(it) }
        )
    }


    override suspend fun toggleFavorite(poi: Poi) {
        val updated = poi.copy(isFavorite = !poi.isFavorite)
        dao.update(PoiEntity.fromDomain(updated))
    }
}

interface PoiRepository {
    fun pois(): Flow<List<Poi>>
    fun favorites(): Flow<List<Poi>>
    suspend fun fetchPois(
        swCorner: String,
        neCorner: String,
        success: () -> Unit = {},
        error: (Throwable) -> Unit = {}
    )
    suspend fun toggleFavorite(poi: Poi)
}