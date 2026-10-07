package com.railshift.passenger.ui.screens.upgrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.railshift.passenger.data.models.TrainClass
import com.railshift.passenger.data.repository.RailshiftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UpgradeViewModel(
    private val repository: RailshiftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpgradeUiState())
    val uiState: StateFlow<UpgradeUiState> = _uiState.asStateFlow()

    fun onScanSuccess(ticketNumber: String) {
        val ticket = repository.getScannedPaperTicket(ticketNumber)
        if (ticket != null) {
            _uiState.update {
                it.copy(
                    isTicketScanned = true,
                    paperTicket = ticket
                )
            }
        }
    }

    fun selectClass(trainClass: TrainClass) {
        _uiState.update { it.copy(selectedClass = trainClass) }
    }

    fun selectTrain(trainNumber: String, trainName: String, departure: String) {
        _uiState.update {
            it.copy(
                selectedTrainNumber = trainNumber,
                selectedTrainName = trainName,
                selectedTrainDeparture = departure
            )
        }
    }

    fun confirmUpgrade(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpgrading = true) }
            val result = repository.upgradeTicket(
                ticketNumber = _uiState.value.paperTicket.ticketNumber,
                trainNumber = _uiState.value.selectedTrainNumber,
                targetClass = _uiState.value.selectedClass
            )
            result.onSuccess { trip ->
                _uiState.update { it.copy(isUpgrading = false, upgradedTrip = trip) }
                onSuccess()
            }.onFailure {
                _uiState.update { it.copy(isUpgrading = false) }
            }
        }
    }
}
