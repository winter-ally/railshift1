package com.railshift.passenger.ui.screens.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.railshift.passenger.data.repository.RailshiftRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookingsViewModel(
    private val repository: RailshiftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsUiState(isLoading = true))
    val uiState: StateFlow<BookingsUiState> = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    private fun loadBookings() {
        viewModelScope.launch {
            combine(
                repository.getUpcomingTrips(),
                repository.getPastTrips()
            ) { active, past ->
                BookingsUiState(
                    selectedTab = _uiState.value.selectedTab,
                    activeTrips = active,
                    pastTrips = past,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun selectTab(tab: BookingsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}
