package com.hakankuru.eventhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hakankuru.eventhub.presentation.ui.main.MainViewModel

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isLoading by viewModel.isLoading.collectAsState()
            val startDestination by viewModel.startDestination.collectAsState()

            com.hakankuru.eventhub.presentation.ui.theme.EventhubTheme {
                if (isLoading || startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    androidx.compose.runtime.key(startDestination) {
                        com.hakankuru.eventhub.presentation.navigation.EventHubApp(
                            startDestination = startDestination!!,
                            onLogout = { viewModel.logout() }
                        )
                    }
                }
            }
        }
    }
}