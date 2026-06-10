package com.hakankuru.eventhub.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hakankuru.eventhub.domain.model.RoleGuard
import com.hakankuru.eventhub.presentation.ui.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToClubManagement: (Long) -> Unit, // Navigation tetikleyicisi ekledik
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Profil") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Profil Bilgileri", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            state.userProfile?.let { profile ->
                Text("İsim: ${profile.name}", style = MaterialTheme.typography.bodyLarge)
                Text("E-posta: ${profile.email}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // RoleGuard ile ADMIN kulüp id'lerini buluyoruz
                val adminClubs = RoleGuard.adminClubIds(profile)

                if (adminClubs.isNotEmpty()) {
                    Button(
                        onClick = {
                            // Admin olduğu ilk kulübün yönetim sayfasına yönlendiriyoruz
                            onNavigateToClubManagement(adminClubs.first())
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Kulüp Yönetimi")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLogout) {
                Text("Çıkış Yap")
            }
        }
    }
}