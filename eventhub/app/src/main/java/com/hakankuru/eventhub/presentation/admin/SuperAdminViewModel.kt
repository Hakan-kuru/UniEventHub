package com.hakankuru.eventhub.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.request.AssignAdminRequest
import com.hakankuru.eventhub.data.remote.response.AdminResponse
import com.hakankuru.eventhub.data.remote.response.AdminUserDto
import com.hakankuru.eventhub.domain.repository.SuperAdminRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── UI Action State (snackbar / loading tetikleyici) ─────────────────────────
sealed class SuperAdminUiState {
    object Initial  : SuperAdminUiState()
    object Loading  : SuperAdminUiState()
    data class Success(val message: String) : SuperAdminUiState()
    data class Error(val message: String)   : SuperAdminUiState()
}

@HiltViewModel
class SuperAdminViewModel @Inject constructor(
    private val superAdminRepository: SuperAdminRepository
) : ViewModel() {

    // ── Action state (assign/remove sonuclari) ────────────────────────────────
    private val _uiState = MutableStateFlow<SuperAdminUiState>(SuperAdminUiState.Initial)
    val uiState: StateFlow<SuperAdminUiState> = _uiState

    // ── Tum kullanici listesi (backend'den ham) ───────────────────────────────
    private val _allUsers = MutableStateFlow<List<AdminUserDto>>(emptyList())

    // ── Yalnizca admin listesi ────────────────────────────────────────────────
    private val _admins = MutableStateFlow<List<AdminResponse>>(emptyList())
    val admins: StateFlow<List<AdminResponse>> = _admins

    // ── Arama sozcugu ─────────────────────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // ── Liste yukleniyor mu? ──────────────────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ── Filtrelenmis kullanici listesi (client-side search) ───────────────────
    val filteredUsers: StateFlow<List<AdminUserDto>> =
        combine(_allUsers, _searchQuery) { users, query ->
            if (query.isBlank()) users
            else users.filter {
                it.email.contains(query.trim(), ignoreCase = true) ||
                it.name.contains(query.trim(), ignoreCase = true)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // ── Admin sayisi KPI ──────────────────────────────────────────────────────
    val adminCount: StateFlow<Int> =
        combine(_allUsers) { (users) ->
            users.count { it.role == "ADMIN" }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    init {
        loadUsers()
        fetchAdmins()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /** Tum kullanicilari yukler (user panel icin) */
    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = superAdminRepository.getAllUsers()) {
                is Result.Success -> _allUsers.value = result.data
                is Result.Error   -> _uiState.value = SuperAdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
            _isLoading.value = false
        }
    }

    /** Yalnizca ADMINleri getirir */
    fun fetchAdmins() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = superAdminRepository.getAllAdmins()) {
                is Result.Success -> _admins.value = result.data
                is Result.Error   -> _uiState.value = SuperAdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
            _isLoading.value = false
        }
    }

    /** Email + universityId ile ADMIN atar */
    fun assignAdmin(email: String, universityId: Long) {
        viewModelScope.launch {
            _uiState.value = SuperAdminUiState.Loading
            val request = AssignAdminRequest(email = email, universityId = universityId)
            when (val result = superAdminRepository.assignAdmin(request)) {
                is Result.Success -> {
                    _uiState.value = SuperAdminUiState.Success("$email admin olarak atandi!")
                    loadUsers()
                    fetchAdmins()
                }
                is Result.Error   -> _uiState.value = SuperAdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }

    /** Admin rolunu kaldirir */
    fun removeAdmin(userId: Long) {
        viewModelScope.launch {
            _uiState.value = SuperAdminUiState.Loading
            when (val result = superAdminRepository.removeAdmin(userId)) {
                is Result.Success -> {
                    _uiState.value = SuperAdminUiState.Success("Admin rolu kaldirildi.")
                    loadUsers()
                    fetchAdmins()
                }
                is Result.Error   -> _uiState.value = SuperAdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }

    /** Arama sorgusunu gunceller */
    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    /** Snackbar gosterildikten sonra state'i sifirlar */
    fun resetState() {
        _uiState.value = SuperAdminUiState.Initial
    }
}