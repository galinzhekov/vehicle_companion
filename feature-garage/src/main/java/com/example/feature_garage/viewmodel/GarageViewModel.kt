package com.example.feature_garage.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.local.FileManager
import com.example.core_data.repository.VehicleRepository
import com.example.core_domain.model.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val repo: VehicleRepository,
    private val fileManager: FileManager
) : ViewModel() {

    val vehicles: StateFlow<List<Vehicle>> =
        repo.vehicles.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun add(vehicle: Vehicle) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repo.add(vehicle)
        }
    }
    fun update(vehicle: Vehicle) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repo.update(vehicle)
        }
    }
    fun delete(vehicle: Vehicle) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            vehicle.imageUri?.let { fileManager.deleteImage(it) }
            repo.delete(vehicle)
        }
    }

    fun handlePickedImage(uri: Uri): String? {
        return fileManager.saveImage(uri)
    }

}
