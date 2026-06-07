package com.hakankuru.eventhub.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            // isLoggedIn ve role'ü aynı anda dinle
            combine(
                sessionManager.getLoginState(),
                sessionManager.getUserRole()
            ) { isLoggedIn, role ->
                Pair(isLoggedIn, role)
            }.collect { (isLoggedIn, role) ->
                _startDestination.value = when {
                    !isLoggedIn          -> "login"
                    role == "SUPER_ADMIN" -> "admin_dashboard"  // Admin direkt kendi paneline gider
                    else                  -> "home"              // Normal kullanıcı home'a gider
                }
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }
}
