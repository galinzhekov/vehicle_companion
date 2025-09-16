package com.example.feature_places.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.core_domain.model.Poi
import com.example.feature_places.R
import com.example.feature_places.viewmodel.PlacesViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacesScreen(viewModel: PlacesViewModel = hiltViewModel()) {
    val pois by viewModel.pois.collectAsState()
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var selectedPoi by remember { mutableStateOf<Poi?>(null) }

    Scaffold { padding ->
        if (pois.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No places loaded", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Button(onClick = { viewModel.refresh() }) { Text("Refresh") }
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(pois, key = { it.id }) { poi ->
                    PoiCard(
                        poi = poi,
                        onClick = {
                            selectedPoi = poi
                            coroutineScope.launch { sheetState.show() }
                        },
                        onFavorite = { viewModel.toggleFavorite(poi) }
                    )
                }
            }
        }
    }

    if (selectedPoi != null) {
        ModalBottomSheet(
            onDismissRequest = {
                selectedPoi = null
                coroutineScope.launch { sheetState.hide() }
            },
            sheetState = sheetState
        ) {
            PlaceDetailSheet(
                poi = selectedPoi!!,
                map = viewModel.getStaticMapUrl(selectedPoi!!.latitude, selectedPoi!!.longitude),
                onClose = {
                    selectedPoi = null
                    coroutineScope.launch { sheetState.hide() }
                },
                onToggleFavorite = {
                    viewModel.toggleFavorite(selectedPoi!!)
                },
                onOpenInBrowser = {
                    val intent = Intent(Intent.ACTION_VIEW, selectedPoi!!.url.toUri())
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun PlaceDetailSheet(
    poi: Poi,
    map: String,
    onClose: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpenInBrowser: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(poi.name, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(poi.category, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        RatingStar(poi.rating)
        Spacer(Modifier.height(12.dp))

        if (poi.imageUrl.isNotBlank()) {
            Image(
                painter = rememberAsyncImagePainter(poi.imageUrl),
                contentDescription = poi.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            AsyncImage(
                model = map,
                contentDescription = "Static map",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Icon(
                painter = painterResource(R.drawable.ic_pin),
                contentDescription = "POI pin",
                tint = Color.Red,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center)
            )
        }


        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onOpenInBrowser) { Text("Open in Browser") }
            OutlinedButton(onClick = onToggleFavorite) {
                Text(if (poi.isFavorite) "Unsave" else "Save")
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onClose) { Text("Close") }
        }
    }
}


