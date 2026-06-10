package com.hakankuru.eventhub.presentation.clubs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.response.ClubMemberManagementResponse
import com.hakankuru.eventhub.data.remote.response.UserSearchResponse
import com.hakankuru.eventhub.domain.repository.ClubMemberRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ClubMemberManagementUiState(
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val members: List<ClubMemberManagementResponse> = emptyList(),
    val searchResults: List<UserSearchResponse> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ClubMemberManagementViewModel @Inject constructor(
    private val repository: ClubMemberRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubMemberManagementUiState())
    val uiState: StateFlow<ClubMemberManagementUiState> = _uiState.asStateFlow()

    private var currentClubId: Long = -1
    private var searchJob: Job? = null

    fun initClubId(clubId: Long) {
        if (currentClubId == clubId) return
        currentClubId = clubId
        fetchMembers()
    }

    fun fetchMembers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getClubMembers(currentClubId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, members = result.data) }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> Unit
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // Debounce mantığı: Kullanıcı yazmayı bıraktıktan 500ms sonra istek atılır
        searchJob?.cancel()
        if (query.trim().length >= 3) {
            searchJob = viewModelScope.launch {
                delay(500)
                searchUsers(query.trim())
            }
        } else {
            _uiState.update { it.copy(searchResults = emptyList()) }
        }
    }

    private suspend fun searchUsers(email: String) {
        when (val result = repository.searchUsersForClub(currentClubId, email)) {
            is Result.Success -> {
                _uiState.update { it.copy(searchResults = result.data) }
            }
            is Result.Error -> {
                _uiState.update { it.copy(error = result.message) }
            }
            else -> Unit
        }
    }

    fun addMember(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, error = null, successMessage = null) }
            when (val result = repository.addMemberToClub(currentClubId, email)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isActionLoading = false,
                            searchQuery = "",
                            searchResults = emptyList(),
                            successMessage = "Üye başarıyla eklendi."
                        )
                    }
                    fetchMembers() // Başarılı işlem sonrası otomatik refresh
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isActionLoading = false, error = result.message) }
                }
                else -> Unit
            }
        }
    }

    fun removeMember(userId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionLoading = true, error = null, successMessage = null) }
            when (val result = repository.removeMemberFromClub(currentClubId, userId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isActionLoading = false, successMessage = "Üye kulüpten çıkarıldı.") }
                    fetchMembers() // Başarılı işlem sonrası otomatik refresh
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isActionLoading = false, error = result.message) }
                }
                else -> Unit
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}