package com.hakankuru.eventhub.presentation.ui.superadmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.request.ClubCreateRequest
import com.hakankuru.eventhub.data.remote.response.ClubResponse
import com.hakankuru.eventhub.domain.repository.SuperAdminRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SuperAdminState(
    val clubs: List<ClubResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SuperAdminViewModel @Inject constructor(
    private val superAdminRepository: SuperAdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SuperAdminState())
    val state: StateFlow<SuperAdminState> = _state.asStateFlow()

    init {
        fetchAllClubs()
    }

    fun fetchAllClubs() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = superAdminRepository.getAllClubs()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(clubs = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun createClub(name: String, description: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val request = ClubCreateRequest(name, description)
            when (val result = superAdminRepository.createClub(request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Kulüp başarıyla oluşturuldu."
                    )
                    fetchAllClubs() // Yenile
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun assignClubAdmin(clubId: Long, email: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = superAdminRepository.assignClubAdmin(clubId, email)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Admin başarıyla atandı."
                    )
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(error = null, successMessage = null)
    }
}
