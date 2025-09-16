package com.example.feature_garage.ui

import com.example.core_domain.model.Vehicle
import com.example.feature_garage.viewmodel.GarageViewModel

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(viewModel: GarageViewModel = hiltViewModel()) {
    val vehicles by viewModel.vehicles.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var editingVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var pickedImageUri by remember { mutableStateOf<String?>(null) }


    val launcher = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        uri?.let {
            pickedImageUri = viewModel.handlePickedImage(it)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingVehicle = Vehicle(name = "", make = "", model = "", year = 2000)
                pickedImageUri = null
                coroutineScope.launch { sheetState.show() }
            }) { Icon(Icons.Default.Add, contentDescription = "Add") }
        }
    ) { padding ->
        if (vehicles.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Your garage is empty", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    editingVehicle = Vehicle(name = "", make = "", model = "", year = 2000)
                    pickedImageUri = null
                    coroutineScope.launch { sheetState.show() }
                }) { Text("Add Vehicle") }
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(vehicles, key = { it.id }) { v ->
                    VehicleCard(v, onEdit = {
                        editingVehicle = v
                        pickedImageUri = v.imageUri
                        coroutineScope.launch { sheetState.show() }
                    })
                }
            }
        }
    }

    if (editingVehicle != null) {
        ModalBottomSheet(
            modifier = Modifier.statusBarsPadding(),
            onDismissRequest = {
                editingVehicle = null
                pickedImageUri = null
                coroutineScope.launch { sheetState.hide() }
            },
            sheetState = sheetState,
        ) {
            AddEditVehicleSheet(
                initial = editingVehicle,
                pickedImageUri = pickedImageUri,
                onDismiss = {
                    editingVehicle = null
                    pickedImageUri = null
                    coroutineScope.launch { sheetState.hide() }
                },
                onPickImage = { launcher.launch("image/*") },
                onSave = { vehicle ->
                    val withImage = vehicle.copy(imageUri = pickedImageUri)
                    if (vehicle.id == 0) viewModel.add(withImage) else viewModel.update(withImage)
                    editingVehicle = null
                    pickedImageUri = null
                    coroutineScope.launch { sheetState.hide() }
                },
                onDelete = { v ->
                    viewModel.delete(v)
                    editingVehicle = null
                    pickedImageUri = null
                    coroutineScope.launch { sheetState.hide() }
                }
            )
        }
    }
}


@Composable
fun AddEditVehicleSheet(
    initial: Vehicle?,
    pickedImageUri: String?,
    onDismiss: () -> Unit,
    onPickImage: () -> Unit,
    onSave: (Vehicle) -> Unit,
    onDelete: (Vehicle) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var make by remember { mutableStateOf(initial?.make ?: "") }
    var model by remember { mutableStateOf(initial?.model ?: "") }
    var yearText by remember { mutableStateOf(initial?.year?.toString() ?: "") }
    var vin by remember { mutableStateOf(initial?.vin ?: "") }
    var fuelType by remember { mutableStateOf(initial?.fuelType ?: "") }

    val isEditMode = initial != null && initial.id != 0
    val valid = name.isNotBlank() &&
            make.isNotBlank() &&
            model.isNotBlank() &&
            yearText.toIntOrNull()?.let { it in 1886..2100 } == true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            if (isEditMode) "Edit Vehicle" else "Add Vehicle",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = make,
            onValueChange = { make = it },
            label = { Text("Make") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Model") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = yearText,
            onValueChange = { yearText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Year") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = vin,
            onValueChange = { vin = it },
            label = { Text("VIN") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = fuelType,
            onValueChange = { fuelType = it },
            label = { Text("Fuel Type") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onPickImage) { Text("Pick Image") }
            Spacer(Modifier.width(12.dp))
            if (!pickedImageUri.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(pickedImageUri),
                    contentDescription = "vehicle image",
                    modifier = Modifier.size(64.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val vehicle = Vehicle(
                        id = initial?.id ?: 0,
                        name = name.trim(),
                        make = make.trim(),
                        model = model.trim(),
                        year = yearText.toIntOrNull() ?: 2000,
                        vin = vin.trim(),
                        fuelType = fuelType.trim(),
                        imageUri = pickedImageUri
                    )
                    onSave(vehicle)
                },
                enabled = valid
            ) {
                Text("Save")
            }
        }

        if (isEditMode) {
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { onDelete(initial!!) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Delete Vehicle")
            }
        }
    }
}
