package com.hakankuru.eventhub.presentation.clubs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubAdminCreateEventScreen(
    clubId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ClubAdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startAt by remember { mutableStateOf("") }
    var endAt by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is ClubAdminUiState.Success) {
            viewModel.resetState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yeni Etkinlik Oluştur") },
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
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Etkinlik Başlığı") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Açıklama") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = startAt,
                onValueChange = { startAt = it },
                label = { Text("Başlangıç Zamanı (ISO-8601 vb.)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = endAt,
                onValueChange = { endAt = it },
                label = { Text("Bitiş Zamanı (ISO-8601 vb.)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = capacity,
                onValueChange = { capacity = it },
                label = { Text("Kapasite") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (uiState is ClubAdminUiState.Error) {
                Text(
                    text = (uiState as ClubAdminUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = { 
                    viewModel.createEvent(
                        clubId = clubId,
                        title = title,
                        description = description,
                        startAt = startAt,
                        endAt = endAt,
                        capacity = capacity.toIntOrNull() ?: 0
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && description.isNotBlank() && uiState !is ClubAdminUiState.Loading
            ) {
                if (uiState is ClubAdminUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Oluştur")
                }
            }
        }
    }
}
