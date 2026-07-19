package com.hakankuru.eventhub.presentation.clubs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hakankuru.eventhub.data.remote.response.ClubMemberResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubAdminMembersScreen(
    clubId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ClubAdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val members by viewModel.members.collectAsState()

    var newMemberEmail by remember { mutableStateOf("") }

    LaunchedEffect(clubId) {
        viewModel.fetchMembers(clubId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Üyeleri Yönet") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Add member section
            Text("Yeni Üye Ekle / Onayla", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newMemberEmail,
                    onValueChange = { newMemberEmail = it },
                    placeholder = { Text("Kullanıcı E-posta") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addMember(clubId, newMemberEmail)
                        newMemberEmail = ""
                    },
                    enabled = newMemberEmail.isNotBlank() && uiState !is ClubAdminUiState.Loading
                ) {
                    Text("Ekle")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (uiState is ClubAdminUiState.Error) {
                Text(
                    text = (uiState as ClubAdminUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else if (uiState is ClubAdminUiState.Success) {
                Text(
                    text = (uiState as ClubAdminUiState.Success).message,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text("Mevcut Üyeler", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState is ClubAdminUiState.Loading && members.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(members) { member ->
                        MemberItem(
                            member = member,
                            onRemove = { viewModel.removeMember(clubId, member.userId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemberItem(member: ClubMemberResponse, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(member.name.ifBlank { member.email }, style = MaterialTheme.typography.bodyLarge)
                Text(member.email, style = MaterialTheme.typography.bodySmall)
                Text("Rol: ${member.role}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Çıkar", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
