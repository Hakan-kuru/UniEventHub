package com.hakankuru.eventhub.presentation.clubs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.request.EventCreateRequest
import com.hakankuru.eventhub.data.remote.response.ClubMemberResponse
import com.hakankuru.eventhub.domain.repository.ClubRepository
import com.hakankuru.eventhub.domain.repository.EventRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubAdminViewModel @Inject constructor(
    private val clubRepository: ClubRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClubAdminUiState>(ClubAdminUiState.Initial)
    val uiState: StateFlow<ClubAdminUiState> = _uiState

    private val _members = MutableStateFlow<List<ClubMemberResponse>>(emptyList())
    val members: StateFlow<List<ClubMemberResponse>> = _members

    fun createEvent(clubId: Long, title: String, description: String, startAt: String, endAt: String, capacity: Int) {
        viewModelScope.launch {
            _uiState.value = ClubAdminUiState.Loading
            val request = EventCreateRequest(
                clubId = clubId, 
                title = title, 
                description = description, 
                startAt = startAt, 
                endAt = endAt, 
                capacity = capacity
            )
            when (val result = eventRepository.createEvent(request)) {
                is Result.Success -> _uiState.value = ClubAdminUiState.Success("Etkinlik başarıyla oluşturuldu!")
                is Result.Error -> _uiState.value = ClubAdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }

    fun fetchMembers(clubId: Long) {
        viewModelScope.launch {
            _uiState.value = ClubAdminUiState.Loading
            when (val result = clubRepository.getClubMembers(clubId)) {
                is Result.Success -> {
                    _members.value = result.data
                    _uiState.value = ClubAdminUiState.Initial
                }
                is Result.Error -> _uiState.value = ClubAdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }

    fun removeMember(clubId: Long, userId: Long) {
        viewModelScope.launch {
            _uiState.value = ClubAdminUiState.Loading
            when (val result = clubRepository.removeMember(clubId, userId)) {
                is Result.Success -> {
                    _uiState.value = ClubAdminUiState.Success("Üye başarıyla çıkarıldı!")
                    fetchMembers(clubId) // Refresh list
                }
                is Result.Error -> _uiState.value = ClubAdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }
    
    fun addMember(clubId: Long, email: String, role: String = "MEMBER") {
        viewModelScope.launch {
            _uiState.value = ClubAdminUiState.Loading
            when (val result = clubRepository.addMember(clubId, email, role)) {
                is Result.Success -> {
                    _uiState.value = ClubAdminUiState.Success("Üye eklendi!")
                    fetchMembers(clubId) // Refresh list
                }
                is Result.Error -> _uiState.value = ClubAdminUiState.Error(result.message)
                is Result.Loading -> Unit
            }
        }
    }

    fun resetState() {
        _uiState.value = ClubAdminUiState.Initial
    }
}

sealed class ClubAdminUiState {
    object Initial : ClubAdminUiState()
    object Loading : ClubAdminUiState()
    data class Success(val message: String) : ClubAdminUiState()
    data class Error(val message: String) : ClubAdminUiState()
}
