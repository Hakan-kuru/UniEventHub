package com.hakankuru.eventhub.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hakankuru.eventhub.domain.model.RoleGuard
import com.hakankuru.eventhub.presentation.admin.AdminCreateClubScreen
import com.hakankuru.eventhub.presentation.admin.AdminDashboardScreen
import com.hakankuru.eventhub.presentation.admin.AdminLoginScreen
import com.hakankuru.eventhub.presentation.admin.SuperAdminPanelScreen
import com.hakankuru.eventhub.presentation.clubs.ClubAdminCreateEventScreen
import com.hakankuru.eventhub.presentation.clubs.ClubAdminMembersScreen
import com.hakankuru.eventhub.presentation.clubs.ClubsScreen
import com.hakankuru.eventhub.presentation.home.HomeScreen
import com.hakankuru.eventhub.presentation.profile.ProfileScreen
import com.hakankuru.eventhub.presentation.ui.session.SessionState
import com.hakankuru.eventhub.presentation.ui.session.SessionViewModel

@Composable
fun EventHubApp(
    startDestination: String = Route.Login.route,
    onLogout: () -> Unit
) {
    val navController       = rememberNavController()
    val sessionViewModel    = hiltViewModel<SessionViewModel>()
    val sessionState        by sessionViewModel.sessionState.collectAsState()

    val navBackStackEntry   by navController.currentBackStackEntryAsState()
    val currentDestination  = navBackStackEntry?.destination

    val bottomNavRoutes = setOf(
        Route.Home.route,
        Route.Explore.route,
        Route.Clubs.route,
        Route.Profile.route
    )
    val isBottomNavVisible = currentDestination?.route in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (isBottomNavVisible) {
                NavigationBar {
                    val items = listOf(
                        Triple(Route.Home.route,    "Ana Sayfa", Icons.Filled.Home),
                        Triple(Route.Explore.route, "Keşfet",   Icons.Filled.Search),
                        Triple(Route.Clubs.route,   "Kulüpler", Icons.Filled.List),
                        Triple(Route.Profile.route, "Profil",   Icons.Filled.Person)
                    )
                    items.forEach { (route, title, icon) ->
                        NavigationBarItem(
                            icon     = { Icon(icon, contentDescription = title) },
                            label    = { Text(title) },
                            selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                            onClick  = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding)
        ) {
            // ── Auth ──────────────────────────────────────────────────────────
            composable(Route.Login.route) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        com.hakankuru.eventhub.presentation.ui.screen.LoginScreen(
                            onLoginSuccess = {
                                sessionViewModel.fetchProfile()
                                navController.navigate(Route.Home.route) {
                                    popUpTo(Route.Login.route) { inclusive = true }
                                }
                            },
                            onGoRegister = { navController.navigate(Route.Register.route) }
                        )
                    }
                    TextButton(
                        onClick  = { navController.navigate(Route.AdminLogin.route) },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp)
                    ) {
                        Text("Yönetici Girişi (Admin Panel)")
                    }
                }
            }

            composable(Route.Register.route) {
                com.hakankuru.eventhub.presentation.ui.screen.RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Register.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── Admin Login — yetkili giriş: profile geldikten sonra navigate ──
            composable(Route.AdminLogin.route) {
                // sessionState burada izleniyor — fetchProfile() bitince Authenticated gelir
                val adminSessionState by sessionViewModel.sessionState.collectAsState()

                // fetchProfile() tamamlanınca ve SUPER_ADMIN ise dashboard'a geç
                LaunchedEffect(adminSessionState) {
                    if (adminSessionState is SessionState.Authenticated) {
                        // SUPER_ADMIN değilse AdminLoginScreen kendi hata mesajını gösteriyor
                    }
                }

                AdminLoginScreen(
                    onLoginSuccess = {
                        // Sadece fetchProfile tetikle — navigate yukarıdaki LaunchedEffect yapar
                        sessionViewModel.fetchProfile()
                    }
                )
            }

            // ── User App — Bottom Nav ─────────────────────────────────────────
            composable(Route.Home.route) {
                HomeScreen()
            }

            composable(Route.Explore.route) {
                ExploreScreen()
            }

            composable(Route.Clubs.route) {
                ClubsScreen(
                    onNavigateToCreateEvent = { clubId ->
                        navController.navigate(Route.ClubAdminCreateEvent.createRoute(clubId))
                    },
                    onNavigateToMembers = { clubId ->
                        navController.navigate(Route.ClubAdminMembers.createRoute(clubId))
                    }
                )
            }

            composable(Route.Profile.route) {
                ProfileScreen(
                    onLogout = {
                        sessionViewModel.logout()
                        onLogout()
                        navController.navigate(Route.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // ── Admin Panel — SUPER_ADMIN Guard ───────────────────────────────
            composable(Route.AdminDashboard.route) {
                when (val state = sessionState) {
                    is SessionState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is SessionState.Authenticated -> {
                        UnauthorizedScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    else -> {
                        UnauthorizedScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }

            composable(Route.AdminCreateClub.route) {
                when (val state = sessionState) {
                    is SessionState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is SessionState.Authenticated -> {
                        UnauthorizedScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    else -> {
                        UnauthorizedScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }

            composable(Route.SuperAdminPanel.route) {
                when (val state = sessionState) {
                    is SessionState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is SessionState.Authenticated -> {
                        UnauthorizedScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    else -> {
                        UnauthorizedScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }

            // ── Club Admin Panel ──────────────────────────────────────────────
            composable(Route.ClubAdminCreateEvent.route) { backStackEntry ->
                val clubId  = backStackEntry.arguments?.getString("clubId")?.toLongOrNull() ?: 0L
                val profile = (sessionState as? SessionState.Authenticated)?.profile
                if (RoleGuard.isClubAdmin(profile, clubId)) {
                    ClubAdminCreateEventScreen(
                        clubId         = clubId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                } else {
                    UnauthorizedScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Route.ClubAdminMembers.route) { backStackEntry ->
                val clubId  = backStackEntry.arguments?.getString("clubId")?.toLongOrNull() ?: 0L
                val profile = (sessionState as? SessionState.Authenticated)?.profile
                if (RoleGuard.isClubAdmin(profile, clubId)) {
                    ClubAdminMembersScreen(
                        clubId         = clubId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                } else {
                    UnauthorizedScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
