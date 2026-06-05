package com.hakankuru.eventhub.presentation.clubs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.tooling.data.Group
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hakankuru.eventhub.data.remote.response.ClubResponse
import com.hakankuru.eventhub.domain.model.RoleGuard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsScreen(
    onNavigateToCreateEvent: (Long) -> Unit = {},
    onNavigateToMembers: (Long) -> Unit = {},
    viewModel: ClubsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kulüpler") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is ClubsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ClubsUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadData() }) { Text("Tekrar Dene") }
                    }
                }

                is ClubsUiState.Success -> {
                    val profile  = state.profile
                    val clubs    = state.clubs
                    val adminIds = RoleGuard.adminClubIds(profile)
                    val memberIds = RoleGuard.memberClubIds(profile)

                    if (clubs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Henüz kulüp bulunmuyor.", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(clubs, key = { it.clubId }) { club ->
                                val isAdmin  = club.clubId in adminIds
                                val isMember = club.clubId in memberIds
                                ClubCard(
                                    club = club,
                                    isAdmin = isAdmin,
                                    isMember = isMember,
                                    onCreateEvent = { onNavigateToCreateEvent(club.clubId) },
                                    onManageMembers = { onNavigateToMembers(club.clubId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClubCard(
    club: ClubResponse,
    isAdmin: Boolean,
    isMember: Boolean,
    onCreateEvent: () -> Unit,
    onManageMembers: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isAdmin)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(club.name, style = MaterialTheme.typography.titleMedium)
                if (isAdmin) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Yönetici") }
                    )
                } else if (isMember) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Üye") }
                    )
                }
            }

            if (club.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = club.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Yönetici aksiyonları — sadece admin'e görünür
            if (isAdmin) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onCreateEvent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Etkinlik Ekle")
                    }
                    OutlinedButton(
                        onClick = onManageMembers,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Üyeler")
                    }
                }
            }
        }
    }
}
