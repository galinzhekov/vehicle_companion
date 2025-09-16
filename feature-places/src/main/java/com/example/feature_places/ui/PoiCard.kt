package com.example.feature_places.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.core_domain.model.Poi

@Composable
fun PoiCard(poi: Poi, onClick: () -> Unit, onFavorite: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (poi.imageUrl.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(poi.imageUrl),
                    contentDescription = poi.name,
                    modifier = Modifier.size(72.dp)
                )
            } else {
                Box(
                    modifier = Modifier.size(72.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No\nImage", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(poi.name, style = MaterialTheme.typography.titleMedium)
                Text(poi.category, style = MaterialTheme.typography.bodySmall)
                RatingStar(poi.rating)
            }
            IconButton(onClick = onFavorite) {
                if (poi.isFavorite) Icon(Icons.Filled.Favorite, contentDescription = "Unfavorite")
                else Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorite")
            }
        }
    }
}