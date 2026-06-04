package com.hakankuru.eventhub.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.request.ClubCreateRequest
import com.hakankuru.eventhub.data.remote.response.ClubResponse
import com.hakankuru.eventhub.domain.repository.SuperAdminRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminDashboardData(
    val clubCount: Int = 0,
    val clubs: List<ClubResponse> = emptyList()
)

sealed class AdminUiState {
    object Initial  : AdminUiState()
    object Loading  : AdminUiState()
    data class Success(val message: String) : AdminUiState()
    data class Error(val message: String)   : AdminUiState()
}

@HiltViewModel
class AdminSuperViewModel @Inject constructor(
    private val superAdminRepository: SuperAdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Initial)
    val uiState: StateFlow<AdminUiState> = _uiState

    private val _dashboardData = MutableStateFlow(AdminDashboardData())
    val dashboardData: StateFlow<AdminDashboardData> = _dashboardData

    private val _isLoadingDashboard = MutableStateFlow(false)
    val isLoadingDashboard: StateFlow<Boolean> = _isLoadingDashboard

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoadingDashboard.value = true
            when (val result = superAdminRepository.getAllClubs()) {
                is Result.Success -> {
                    _dashboardData.value = AdminDashboardData(
                        clubCount = result.data.size,
                        clubs     = result.data
                    )
                }
                is Result.Error -> {
                    _uiState.value = AdminUiState.Error(result.message)
                }
                is Result.Loading -> Unit
            }
            _isLoadingDashboard.value = false
        }
    }

    fun createClub(name: String, description: String) {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            val request = ClubCreateRequest(name = name, description = description)
            when (val result = superAdminRepository.createClub(request)) {
                is Result.Success -> {
                    _uiState.value = AdminUiState.Success("Kulüp başarıyla oluşturuldu!")
                    loadDashboard()          // dashboard sayılarını güncelle
                }
                is Result.Error   -> _uiState.value = AdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }

    fun assignAdmin(clubId: Long, email: String) {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            when (val result = superAdminRepository.assignClubAdmin(clubId, email)) {
                is Result.Success -> _uiState.value = AdminUiState.Success("Yönetici başarıyla atandı!")
                is Result.Error   -> _uiState.value = AdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }

    fun resetState() {
        _uiState.value = AdminUiState.Initial
    }
}
