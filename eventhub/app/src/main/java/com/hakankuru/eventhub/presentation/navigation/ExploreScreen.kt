package com.hakankuru.eventhub.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Keşfet") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Etkinlik keşfet", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Yakında: Tüm etkinlikleri burada listeleyebileceksiniz.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
