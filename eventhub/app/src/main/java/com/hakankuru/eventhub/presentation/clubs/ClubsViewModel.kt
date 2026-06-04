package com.hakankuru.eventhub.presentation.clubs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.response.ClubResponse
import com.hakankuru.eventhub.data.remote.response.UserProfileResponse
import com.hakankuru.eventhub.domain.model.RoleGuard
import com.hakankuru.eventhub.domain.repository.ClubRepository
import com.hakankuru.eventhub.domain.repository.UserRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClubsUiState {
    object Loading : ClubsUiState()
    data class Success(
        val clubs: List<ClubResponse>,
        val profile: UserProfileResponse
    ) : ClubsUiState()
    data class Error(val message: String) : ClubsUiState()
}

@HiltViewModel
class ClubsViewModel @Inject constructor(
    private val clubRepository: ClubRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClubsUiState>(ClubsUiState.Loading)
    val uiState: StateFlow<ClubsUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = ClubsUiState.Loading

            // /me ve kulüp listesini paralel çek
            val profileResult = userRepository.getCurrentUser()
            val clubsResult   = clubRepository.getAllClubs()

            if (profileResult is Result.Error) {
                _uiState.value = ClubsUiState.Error(profileResult.message)
                return@launch
            }
            if (clubsResult is Result.Error) {
                _uiState.value = ClubsUiState.Error(clubsResult.message)
                return@launch
            }

            val profile = (profileResult as Result.Success).data
            val clubs   = (clubsResult   as Result.Success).data

            _uiState.value = ClubsUiState.Success(clubs = clubs, profile = profile)
        }
    }

    /** Kullanıcının belirli bir kulüpte admin olup olmadığını döner. */
    fun isAdminOf(clubId: Long): Boolean {
        val state = _uiState.value
        return if (state is ClubsUiState.Success) RoleGuard.isClubAdmin(state.profile, clubId) else false
    }

    /** Kullanıcının belirli bir kulüpte üye olup olmadığını döner. */
    fun isMemberOf(clubId: Long): Boolean {
        val state = _uiState.value
        return if (state is ClubsUiState.Success) RoleGuard.isClubMember(state.profile, clubId) else false
    }
}
