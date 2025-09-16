package com.example.feature_places.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.repository.PoiRepository
import com.example.core_domain.model.Poi
import com.example.feature_places.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(private val repo: PoiRepository) : ViewModel() {
    val pois: StateFlow<List<Poi>> = repo.pois().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val favorites: StateFlow<List<Poi>> = repo.favorites().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val swCorner = "-84.540499,39.079888"
    private val neCorner = "-84.494260,39.113254"

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repo.fetchPois(swCorner, neCorner, error = {

            })
        }
    }
    fun toggleFavorite(poi: Poi) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repo.toggleFavorite(poi)
        }
    }

    fun getStaticMapUrl(lat: Double, lon: Double, width: Int = 320, height: Int = 240, zoom: Int = 14): String {
        return "https://api.mapbox.com/styles/v1/roadtrippers/ciujug7vi002o2inyf5sf4o7b/static/$lon,$lat,$zoom/${width}x$height@2x?access_token=${BuildConfig.MAPS_API_KEY}"
    }
}