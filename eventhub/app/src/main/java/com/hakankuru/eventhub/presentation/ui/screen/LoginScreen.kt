package com.hakankuru.eventhub.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hakankuru.eventhub.presentation.ui.auth.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onGoRegister: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val error = viewModel.error.value
    val isLoading = viewModel.isLoading.value

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Login")

        Spacer(Modifier.height(12.dp))

        TextField(email, { email = it }, label = { Text("Email") })
        TextField(password, { password = it }, label = { Text("Password") })

        error?.let {
            Text(text = it, color = Color.Red)
        }

        Button(
            onClick = {
                viewModel.login(email, password) {
                    onLoginSuccess()
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Loading..." else "Login")
        }

        TextButton(onClick = onGoRegister) {
            Text("Register")
        }
    }
}