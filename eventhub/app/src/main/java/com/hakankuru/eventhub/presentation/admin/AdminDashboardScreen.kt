package com.hakankuru.eventhub.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToCreateClub: () -> Unit = {},
    viewModel: AdminSuperViewModel = hiltViewModel()
) {
    val dashboardData    by viewModel.dashboardData.collectAsState()
    val isLoadingDashboard by viewModel.isLoadingDashboard.collectAsState()
    val uiState          by viewModel.uiState.collectAsState()

    // Snackbar host
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AdminUiState.Error   -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            is AdminUiState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Global Admin Panel") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Hoş Geldiniz, Süper Admin", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))

            if (isLoadingDashboard) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        DashboardCard(
                            title = "Kulüpler",
                            value = dashboardData.clubCount.toString(),
                            icon  = Icons.Filled.List
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Hızlı İşlemler", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToCreateClub,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kulüp Yönetimi (Kulüp Ekle)")
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { viewModel.loadDashboard() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Yenile")
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
