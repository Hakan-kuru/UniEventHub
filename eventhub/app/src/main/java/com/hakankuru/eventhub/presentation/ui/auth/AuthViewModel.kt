package com.hakankuru.eventhub.presentation.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.request.AuthResponse
import com.hakankuru.eventhub.domain.usecase.auth.LoginUseCase
import com.hakankuru.eventhub.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    var isLoading = mutableStateOf(false)
        private set

    var error = mutableStateOf<String?>(null)
        private set

    fun login(
        email: String,
        password: String,
        onSuccess: (AuthResponse) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                error.value = null

                val response = loginUseCase(email, password)

                onSuccess(response)

            } catch (e: Exception) {
                error.value = e.message ?: "Login error"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        departmentId: Long,
        onSuccess: (AuthResponse) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                error.value = null

                val response = registerUseCase(
                    name,
                    email,
                    password,
                    departmentId
                )

                onSuccess(response)

            } catch (e: Exception) {
                error.value = e.message ?: "Register error"
            } finally {
                isLoading.value = false
            }
        }
    }
}