package com.hakankuru.eventhub.presentation.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.local.SessionManager
import com.hakankuru.eventhub.data.remote.response.UserProfileResponse
import com.hakankuru.eventhub.domain.repository.UserRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SessionState {
    object Loading  : SessionState()
    object NoSession : SessionState()
    data class Authenticated(val profile: UserProfileResponse) : SessionState()
    data class Error(val message: String) : SessionState()
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState

    init {
        bootstrap()
    }

    /** App açılışında token var mı kontrol et; varsa /me çağır. */
    fun bootstrap() {
        viewModelScope.launch {
            _sessionState.value = SessionState.Loading
            val isLoggedIn = sessionManager.getLoginState().first()
            if (!isLoggedIn) {
                _sessionState.value = SessionState.NoSession
                return@launch
            }
            fetchProfile()
        }
    }

    /** /me endpoint'ini çağırarak profili günceller. */
    fun fetchProfile() {
        viewModelScope.launch {
            when (val result = userRepository.getCurrentUser()) {
                is Result.Success -> _sessionState.value = SessionState.Authenticated(result.data)
                is Result.Error   -> _sessionState.value = SessionState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }

    /** Profile'ı döner; henüz yüklenmemişse null. */
    val currentProfile: UserProfileResponse?
        get() = (_sessionState.value as? SessionState.Authenticated)?.profile

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _sessionState.value = SessionState.NoSession
        }
    }
}
