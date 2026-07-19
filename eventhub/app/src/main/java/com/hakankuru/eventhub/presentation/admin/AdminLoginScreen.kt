package com.hakankuru.eventhub.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hakankuru.eventhub.domain.model.RoleGuard
import com.hakankuru.eventhub.presentation.ui.auth.AuthViewModel

@Composable
fun AdminLoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var roleError by remember { mutableStateOf<String?>(null) }

    val isLoading by viewModel.isLoading
    val error     by viewModel.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Admin Paneli Girişi", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; roleError = null },
            label = { Text("Admin Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; roleError = null },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Hata mesajları
        val displayError = roleError ?: error
        if (displayError != null) {
            Text(
                text = displayError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = {
                roleError = null
                viewModel.login(email, password) { authResponse ->
                    // GlobalRole kontrolü — sadece SUPER_ADMIN girebilir
                    // authResponse içindeki role bilgisini kontrol et
                    if (authResponse.role == "SUPER_ADMIN") {
                        onLoginSuccess()
                    } else {
                        roleError = "Bu panel yalnızca Süper Admin'e özeldir. Yetkiniz bulunmuyor."
                        // Oturumu temizle — yanlış role ile token saklanmamalı
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Giriş Yap")
            }
        }
    }
}
