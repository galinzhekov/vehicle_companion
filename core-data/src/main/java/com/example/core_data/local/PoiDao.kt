package com.example.core_data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PoiDao {
    @Query("SELECT * FROM poi_table ORDER BY name")
    fun getAll(): Flow<List<PoiEntity>>

    @Query("SELECT * FROM poi_table WHERE isFavorite = 1 ORDER BY name")
    fun getFavorites(): Flow<List<PoiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PoiEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PoiEntity)

    @Update
    suspend fun update(entity: PoiEntity)

    @Query("SELECT * FROM poi_table WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PoiEntity?
}
