package com.hakankuru.eventhub.presentation.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hakankuru.eventhub.data.remote.response.EventResponse
import com.hakankuru.eventhub.domain.repository.EventRepository
import com.hakankuru.eventhub.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExploreUiState {
    object Loading : ExploreUiState()
    data class Success(val events: List<EventResponse>) : ExploreUiState()
    data class Error(val message: String) : ExploreUiState()
}

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _allEvents  = MutableStateFlow<List<EventResponse>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val filteredEvents: StateFlow<List<EventResponse>> =
        combine(_allEvents, _searchQuery) { events, query ->
            if (query.isBlank()) events
            else events.filter {
                it.title.contains(query.trim(), ignoreCase = true) ||
                it.clubName.contains(query.trim(), ignoreCase = true)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = eventRepository.getAllEvents()) {
                is Result.Success -> _allEvents.value = result.data
                is Result.Error   -> _error.value = result.message
                is Result.Loading -> Unit
            }
            _isLoading.value = false
        }
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }
}