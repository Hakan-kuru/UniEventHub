package com.hakankuru.eventhub.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.request.UserUpdateRequest
import com.hakankuru.eventhub.data.remote.response.EventResponse
import com.hakankuru.eventhub.data.remote.response.UserProfileResponse
import com.hakankuru.eventhub.domain.repository.UserRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val userProfile: UserProfileResponse? = null,
    val myEvents: List<EventResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun fetchProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = userRepository.getCurrentUser()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(userProfile = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun fetchMyEvents() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = userRepository.getCurrentUserEvents()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(myEvents = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun updateProfile(name: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val request = UserUpdateRequest(name)
            when (val result = userRepository.updateCurrentUser(request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        userProfile = result.data,
                        isLoading = false,
                        successMessage = "Profil başarıyla güncellendi."
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
