package com.hakankuru.eventhub.presentation.clubs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsScreen() {
    // Mock user permission state
    // In production, this will come from ClubsViewModel holding UserProfileResponse
    val isClubMember = true
    val isClubAdmin = true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kulüpler") },
                actions = {
                    if (isClubMember || isClubAdmin) {
                        IconButton(onClick = { /* TODO: Create Event */ }) {
                            Icon(Icons.Filled.Add, contentDescription = "Etkinlik Oluştur")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (isClubAdmin) {
                FloatingActionButton(onClick = { /* TODO: Manage Members */ }) {
                    Icon(Icons.Filled.Person, contentDescription = "Üyeleri Yönet")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Kulüp İçeriği (Normal User Görebilir)", style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isClubMember) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Club Member Yetkileri", style = MaterialTheme.typography.titleMedium)
                        Text("- Etkinlik oluştur/düzenle")
                        Text("- Katılımcı görüntüle")
                        Text("- Bildirim işlemleri")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isClubAdmin) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Club Admin Yetkileri", style = MaterialTheme.typography.titleMedium)
                        Text("- Kullanıcı email ile arama")
                        Text("- Üye ekle/sil")
                        Text("- Admin atama/alma")
                    }
                }
            }
        }
    }
}
