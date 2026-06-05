package com.hakankuru.eventhub.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hakankuru.eventhub.data.remote.response.AdminUserDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminPanelScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SuperAdminViewModel = hiltViewModel()
) {
    val uiState       by viewModel.uiState.collectAsState()
    val filteredUsers by viewModel.filteredUsers.collectAsState()
    val searchQuery   by viewModel.searchQuery.collectAsState()
    val isLoading     by viewModel.isLoading.collectAsState()
    val adminCount    by viewModel.adminCount.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is SuperAdminUiState.Error   -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            is SuperAdminUiState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    // universityId dialog state
    var pendingAssignUser by remember { mutableStateOf<AdminUserDto?>(null) }
    var universityIdInput by remember { mutableStateOf("") }

    if (pendingAssignUser != null) {
        AlertDialog(
            onDismissRequest = {
                pendingAssignUser = null
                universityIdInput = ""
            },
            title = { Text("Admin Yap") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = pendingAssignUser!!.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = universityIdInput,
                        onValueChange = { universityIdInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Universite ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                val uid = universityIdInput.toLongOrNull()
                TextButton(
                    onClick = {
                        if (uid != null) {
                            viewModel.assignAdmin(pendingAssignUser!!.email, uid)
                            pendingAssignUser = null
                            universityIdInput = ""
                        }
                    },
                    enabled = uid != null
                ) { Text("Ata") }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingAssignUser = null
                    universityIdInput = ""
                }) { Text("Iptal") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Super Admin Paneli") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // KPI Karti
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Toplam Admin",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = adminCount.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Toplam Kullanici",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = filteredUsers.size.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("Email veya isimle ara...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Temizle")
                        }
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(4.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                filteredUsers.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "Kullanici bulunamadi."
                                       else "\"$searchQuery\" ile eslesme yok.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (searchQuery.isNotBlank()) {
                                TextButton(onClick = { viewModel.onSearchChange("") }) {
                                    Text("Aramayı temizle")
                                }
                            }
                        }
                    }
                }

                else -> {
                    val isActionLoading = uiState is SuperAdminUiState.Loading
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredUsers, key = { it.userId }) { user ->
                            UserListCard(
                                user = user,
                                isActionLoading = isActionLoading,
                                onMakeAdmin = { pendingAssignUser = user },
                                onRemoveAdmin = { viewModel.removeAdmin(user.userId) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserListCard(
    user: AdminUserDto,
    isActionLoading: Boolean,
    onMakeAdmin: () -> Unit,
    onRemoveAdmin: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoleBadge(role = user.role)
                    if (user.universityId != null) {
                        Text(
                            text = "Uni #${user.universityId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            when (user.role) {
                "USER" -> {
                    FilledTonalButton(
                        onClick = onMakeAdmin,
                        enabled = !isActionLoading,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Admin Yap", style = MaterialTheme.typography.labelMedium)
                    }
                }
                "ADMIN" -> {
                    FilledTonalButton(
                        onClick = onRemoveAdmin,
                        enabled = !isActionLoading,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        if (isActionLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Kaldir", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                "SUPER_ADMIN" -> {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Korumalı",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(role: String) {
    val bgColor = when (role) {
        "SUPER_ADMIN" -> Color(0xFF4A1942)
        "ADMIN"       -> Color(0xFF1565C0)
        else          -> Color(0xFF546E7A)
    }
    val label = when (role) {
        "SUPER_ADMIN" -> "SUPER ADMIN"
        "ADMIN"       -> "ADMIN"
        else          -> "USER"
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = bgColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}