package com.hakankuru.eventhub.presentation.ui.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.response.ClubMemberResponse
import com.hakankuru.eventhub.domain.repository.ClubRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClubManagementState(
    val members: List<ClubMemberResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ClubManagementViewModel @Inject constructor(
    private val clubRepository: ClubRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ClubManagementState())
    val state: StateFlow<ClubManagementState> = _state.asStateFlow()

    fun fetchClubMembers(clubId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = clubRepository.getClubMembers(clubId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(members = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun addMember(clubId: Long, email: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = clubRepository.addMember(clubId, email)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Üye başarıyla eklendi."
                    )
                    fetchClubMembers(clubId) // Yenile
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun removeMember(clubId: Long, userId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = clubRepository.removeMember(clubId, userId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Üye başarıyla silindi."
                    )
                    fetchClubMembers(clubId) // Yenile
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
