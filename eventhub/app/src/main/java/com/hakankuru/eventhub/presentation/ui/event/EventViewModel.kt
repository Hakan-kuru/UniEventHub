package com.hakankuru.eventhub.presentation.ui.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.request.EventCreateRequest
import com.hakankuru.eventhub.data.remote.response.EventResponse
import com.hakankuru.eventhub.domain.repository.EventRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EventState(
    val clubEvents: List<EventResponse> = emptyList(),
    val allEvents: List<EventResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EventState())
    val state: StateFlow<EventState> = _state.asStateFlow()

    fun fetchEventsByClub(clubId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = eventRepository.getClubEvents(clubId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(clubEvents = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun fetchAllEvents() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = eventRepository.getAllEvents()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(allEvents = result.data, isLoading = false)
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun createEvent(request: EventCreateRequest) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = eventRepository.createEvent(request)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Etkinlik başarıyla oluşturuldu."
                    )
                    fetchEventsByClub(request.clubId) // Yenile
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun joinEvent(eventId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = eventRepository.joinEvent(eventId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Etkinliğe başarıyla katıldınız."
                    )
                    fetchAllEvents()
                }
                is Result.Error -> {
                    _state.value = _state.value.copy(error = result.message, isLoading = false)
                }
                is Result.Loading -> {}
            }
        }
    }

    fun leaveEvent(eventId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = eventRepository.leaveEvent(eventId)) {
                is Result.Success -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Etkinlikten başarıyla ayrıldınız."
                    )
                    fetchAllEvents()
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
