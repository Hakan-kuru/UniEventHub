package com.hakankuru.eventhub.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hakankuru.eventhub.presentation.home.HomeScreen
import com.hakankuru.eventhub.presentation.clubs.ClubsScreen
import com.hakankuru.eventhub.presentation.profile.ProfileScreen
import com.hakankuru.eventhub.presentation.admin.AdminDashboardScreen
import com.hakankuru.eventhub.presentation.admin.AdminLoginScreen
// TODO: Import LoginScreen, RegisterScreen from new auth package

@Composable
fun EventHubApp(
    startDestination: String = Route.Login.route,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isBottomNavVisible = currentDestination?.route in listOf(
        Route.Home.route,
        Route.Explore.route,
        Route.Clubs.route,
        Route.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (isBottomNavVisible) {
                NavigationBar {
                    val items = listOf(
                        Triple(Route.Home.route, "Ana Sayfa", Icons.Filled.Home),
                        Triple(Route.Explore.route, "Keşfet", Icons.Filled.List),
                        Triple(Route.Clubs.route, "Kulüpler", Icons.Filled.List),
                        Triple(Route.Profile.route, "Profil", Icons.Filled.Person)
                    )
                    items.forEach { (route, title, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = title) },
                            label = { Text(title) },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Login.route) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        com.hakankuru.eventhub.presentation.ui.screen.LoginScreen(
                            onLoginSuccess = { navController.navigate(Route.Home.route) { popUpTo(Route.Login.route) { inclusive = true } } },
                            onGoRegister = { navController.navigate(Route.Register.route) }
                        )
                    }
                    TextButton(
                        onClick = { navController.navigate(Route.AdminLogin.route) },
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally).padding(bottom = 16.dp)
                    ) {
                        Text("Yönetici Girişi (Admin Panel)")
                    }
                }
            }
            composable(Route.Register.route) {
                com.hakankuru.eventhub.presentation.ui.screen.RegisterScreen(
                    onRegisterSuccess = { navController.navigate(Route.Login.route) { popUpTo(Route.Register.route) { inclusive = true } } }
                )
            }
            composable(Route.AdminLogin.route) {
                AdminLoginScreen(
                    onLoginSuccess = { navController.navigate(Route.AdminDashboard.route) { popUpTo(Route.AdminLogin.route) { inclusive = true } } }
                )
            }
            composable(Route.Home.route) {
                HomeScreen()
            }
            composable(Route.Explore.route) {
                Text("Explore Screen - TODO")
            }
            composable(Route.Clubs.route) {
                ClubsScreen()
            }
            composable(Route.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        onLogout()
                        navController.navigate(Route.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            
            // Admin Panel
            composable(Route.AdminDashboard.route) {
                AdminDashboardScreen()
            }
            // TODO: Add other Admin screens
        }
    }
}
