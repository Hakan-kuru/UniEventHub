package com.hakankuru.eventhub.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hakankuru.eventhub.presentation.ui.profile.ProfileViewModel

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToSuperAdmin: () -> Unit = {},
    onNavigateToClubManagement: (Long) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }

        state.userProfile?.let { profile ->
            Text(
                text = "Hoş geldin, ${profile.name} 👋",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (profile.globalRole == "SUPER_ADMIN") {
                Button(
                    onClick = onNavigateToSuperAdmin,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("Süper Admin Paneli")
                }
            }

            val adminClubs = profile.clubs.filter { it.clubRole == "ADMIN" }
            if (adminClubs.isNotEmpty()) {
                Text(
                    text = "Yönettiğiniz Kulüpler",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).align(Alignment.Start)
                )
                adminClubs.forEach { club ->
                    OutlinedButton(
                        onClick = { onNavigateToClubManagement(club.clubId) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text("${club.clubName} Yönetimi")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Çıkış Yap")
        }
    }
}