package com.railshift.passenger.ui.screens.platform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.railshift.passenger.data.models.Station
import com.railshift.passenger.data.repository.RailshiftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlatformViewModel(
    private val repository: RailshiftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PlatformUiState(recentStations = repository.getRecentPlatformStations())
    )
    val uiState: StateFlow<PlatformUiState> = _uiState.asStateFlow()

    fun selectStation(station: Station) {
        _uiState.update { it.copy(selectedStation = station) }
    }

    fun bookPlatformTicket(onSuccess: (String) -> Unit) {
        val station = _uiState.value.selectedStation ?: _uiState.value.recentStations.first()
        viewModelScope.launch {
            _uiState.update { it.copy(isBooking = true) }
            val result = repository.bookPlatformTicket(station.code, _uiState.value.journeyDateText)
            result.onSuccess { ticketId ->
                _uiState.update { it.copy(isBooking = false, bookedTicketId = ticketId) }
                onSuccess(ticketId)
            }.onFailure {
                _uiState.update { it.copy(isBooking = false) }
            }
        }
    }
}
