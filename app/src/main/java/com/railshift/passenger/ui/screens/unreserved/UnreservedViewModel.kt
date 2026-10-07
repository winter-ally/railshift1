package com.railshift.passenger.ui.screens.unreserved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.railshift.passenger.data.models.RecentUnreservedTicket
import com.railshift.passenger.data.models.Station
import com.railshift.passenger.data.repository.RailshiftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UnreservedViewModel(
    private val repository: RailshiftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UnreservedUiState(
            fromStation = repository.getStations().find { it.code == "BZA" } ?: Station("BZA", "Vijayawada Jn", "Vijayawada"),
            toStation = repository.getStations().find { it.code == "GNT" } ?: Station("GNT", "Guntur Jn", "Guntur"),
            recentTickets = repository.getRecentUnreservedTickets()
        )
    )
    val uiState: StateFlow<UnreservedUiState> = _uiState.asStateFlow()

    fun swapStations() {
        _uiState.update {
            it.copy(fromStation = it.toStation, toStation = it.fromStation)
        }
    }

    fun incrementAdults() {
        if (_uiState.value.adults < 6) {
            _uiState.update { it.copy(adults = it.adults + 1) }
        }
    }

    fun decrementAdults() {
        if (_uiState.value.adults > 1) {
            _uiState.update { it.copy(adults = it.adults - 1) }
        }
    }

    fun incrementChildren() {
        if (_uiState.value.children < 6) {
            _uiState.update { it.copy(children = it.children + 1) }
        }
    }

    fun decrementChildren() {
        if (_uiState.value.children > 0) {
            _uiState.update { it.copy(children = it.children - 1) }
        }
    }

    fun selectRecentTicket(ticket: RecentUnreservedTicket) {
        val from = repository.getStationByCode(ticket.fromCode) ?: _uiState.value.fromStation
        val to = repository.getStationByCode(ticket.toCode) ?: _uiState.value.toStation
        _uiState.update {
            it.copy(fromStation = from, toStation = to)
        }
    }

    fun bookTicket(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBooking = true) }
            val result = repository.bookUnreservedTicket(
                fromCode = _uiState.value.fromStation.code,
                toCode = _uiState.value.toStation.code,
                adults = _uiState.value.adults,
                children = _uiState.value.children
            )
            result.onSuccess { trip ->
                _uiState.update { it.copy(isBooking = false, bookedTrip = trip) }
                onSuccess()
            }.onFailure {
                _uiState.update { it.copy(isBooking = false) }
            }
        }
    }
}
