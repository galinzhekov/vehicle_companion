package com.example.vehiclecompanion.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.feature_garage.ui.GarageScreen
import com.example.feature_places.ui.PlacesScreen

sealed class Tab(val route: String, val label: String, val icon: @Composable () -> Unit) {
    object Garage : Tab("garage", "Garage", { Icon(Icons.Default.Home, contentDescription = "Garage") })
    object Places : Tab("places", "Places", { Icon(Icons.Default.Place, contentDescription = "Places") })
}

@Composable
fun MainAppHost() {
    val nav = rememberNavController()
    val tabs = listOf(Tab.Garage, Tab.Places)

    Scaffold(
        bottomBar = {
            BottomBar(navController = nav, tabs = tabs)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            NavHost(navController = nav, startDestination = Tab.Garage.route) {
                composable(Tab.Garage.route) { GarageScreen() }
                composable(Tab.Places.route) { PlacesScreen() }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, tabs: List<Tab>) {
    val navBack = navController.currentBackStackEntryAsState()
    val current = navBack.value?.destination?.route
    NavigationBar {
        tabs.forEach { t ->
            NavigationBarItem(
                selected = current == t.route,
                onClick = { navController.navigate(t.route) { popUpTo(navController.graph.startDestinationId) } },
                icon = t.icon,
                label = { Text(t.label) }
            )
        }
    }
}