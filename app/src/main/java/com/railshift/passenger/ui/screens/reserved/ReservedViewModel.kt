package com.railshift.passenger.ui.screens.reserved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.railshift.passenger.data.models.Station
import com.railshift.passenger.data.models.TrainClass
import com.railshift.passenger.data.repository.RailshiftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReservedViewModel(
    private val repository: RailshiftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReservedUiState(
            fromStation = repository.getStations().find { it.code == "BZA" } ?: Station("BZA", "Vijayawada Jn", "Vijayawada"),
            toStation = repository.getStations().find { it.code == "MAS" } ?: Station("MAS", "Chennai Central", "Chennai"),
            recentSearches = repository.getRecentSearches()
        )
    )
    val uiState: StateFlow<ReservedUiState> = _uiState.asStateFlow()

    fun swapStations() {
        _uiState.update {
            it.copy(fromStation = it.toStation, toStation = it.fromStation)
        }
    }

    fun selectDateChip(index: Int) {
        val dateStr = when (index) {
            0 -> "Wed, 07 Oct 2026"
            1 -> "Thu, 08 Oct 2026"
            else -> "Mon, 12 Oct 2026"
        }
        _uiState.update {
            it.copy(selectedDateChipIndex = index, journeyDateText = dateStr)
        }
    }

    fun selectClass(trainClass: TrainClass) {
        _uiState.update { it.copy(selectedClass = trainClass) }
    }

    fun toggleShowAvailableOnly(checked: Boolean) {
        _uiState.update { it.copy(showAvailableOnly = checked) }
    }

    fun toggleFlexibleDates(checked: Boolean) {
        _uiState.update { it.copy(isFlexibleWithDates = checked) }
    }

    fun selectRecentSearch(search: com.railshift.passenger.data.models.RecentSearch) {
        val from = repository.getStationByCode(search.fromCode) ?: _uiState.value.fromStation
        val to = repository.getStationByCode(search.toCode) ?: _uiState.value.toStation
        val trainClass = TrainClass.values().find { it.code == search.classCode } ?: TrainClass.THREE_A

        _uiState.update {
            it.copy(
                fromStation = from,
                toStation = to,
                selectedClass = trainClass,
                journeyDateText = search.dateText
            )
        }
    }

    fun searchTrains() {
        _uiState.update { it.copy(isSearching = true) }
        val results = repository.searchTrains(
            fromCode = _uiState.value.fromStation.code,
            toCode = _uiState.value.toStation.code,
            date = _uiState.value.journeyDateText,
            trainClass = _uiState.value.selectedClass,
            availableOnly = _uiState.value.showAvailableOnly
        )
        _uiState.update {
            it.copy(searchResults = results, isSearching = false)
        }
    }

    fun bookTrain(trainNumber: String, trainClass: TrainClass) {
        viewModelScope.launch {
            // Booked
            _uiState.update { it.copy(bookedTripId = trainNumber) }
        }
    }
}
