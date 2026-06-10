package com.hakankuru.eventhub.presentation.clubs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hakankuru.eventhub.data.remote.response.ClubMemberManagementResponse
import com.hakankuru.eventhub.data.remote.response.UserSearchResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubMemberManagementScreen(
    clubId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ClubMemberManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(clubId) {
        viewModel.initClubId(clubId)
    }

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kulüp Üye Yönetimi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Bölüm 1: Arama ve Ekleme Alanı
            Text(
                text = "E-posta ile Kulübe Üye Ekle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("E-posta adresi yazın (En az 3 karakter)...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Temizle")
                        }
                    }
                },
                singleLine = true
            )

            // Arama Sonuçları Listesi (Dropdown/Kart görünümü gibi üstte listeleme)
            if (uiState.searchResults.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        uiState.searchResults.forEach { user ->
                            SearchUserRow(
                                user = user,
                                isActionLoading = uiState.isActionLoading,
                                onAddClick = { viewModel.addMember(user.email) }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bölüm 2: Mevcut Üyeler Listesi
            Text(
                text = "Mevcut Üyeler",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.members.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Kulübün henüz kayıtlı bir üyesi bulunmuyor.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.members, key = { it.userId }) { member ->
                        MemberManagementCard(
                            member = member,
                            isActionLoading = uiState.isActionLoading,
                            onRemoveClick = { viewModel.removeMember(member.userId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchUserRow(
    user: UserSearchResponse,
    isActionLoading: Boolean,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = user.email, style = MaterialTheme.typography.bodySmall)
            user.departmentName?.let {
                Text(text = it, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
        IconButton(onClick = onAddClick, enabled = !isActionLoading) {
            Icon(Icons.Filled.PersonAdd, contentDescription = "Üye Ekle", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun MemberManagementCard(
    member: ClubMemberManagementResponse,
    isActionLoading: Boolean,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (member.clubRole == "ADMIN")
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceCard()
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = member.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(text = member.email, style = MaterialTheme.typography.bodyMedium)
                member.departmentName?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (member.clubRole == "ADMIN") {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = Color(0xFF1565C0)
                ) {
                    Text(
                        text = "Kulüp Yöneticisi",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                IconButton(onClick = onRemoveClick, enabled = !isActionLoading) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Üyeyi Sil",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// Yardımcı M3 genişletme fonksiyonu
@Composable
fun ColorScheme.surfaceCard(): Color = this.surfaceVariant.copy(alpha = 0.5f)