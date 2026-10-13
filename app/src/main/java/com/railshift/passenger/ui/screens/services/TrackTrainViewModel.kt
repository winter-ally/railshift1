package com.railshift.passenger.ui.screens.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.railshift.passenger.data.models.LiveTrainResult
import com.railshift.passenger.data.models.TrainDirectory
import com.railshift.passenger.data.network.LiveTrainApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackTrainViewModel(
    private val apiClient: LiveTrainApiClient = LiveTrainApiClient()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TrackTrainUiState(
            isConfiguredWithKey = true
        )
    )
    val uiState: StateFlow<TrackTrainUiState> = _uiState.asStateFlow()

    private var coldStartJob: Job? = null

    fun onTrainNumberChange(input: String) {
        val trimmed = input.trim()
        val digitsOnly = trimmed.filter { it.isDigit() }

        val suggestions = if (trimmed.isNotEmpty()) {
            TrainDirectory.searchTrainsByQuery(trimmed).take(5)
        } else {
            emptyList()
        }

        val resolvedNumber = if (digitsOnly.length in 4..5) {
            digitsOnly.take(5)
        } else {
            TrainDirectory.findTrainByNumberOrName(trimmed)?.number ?: digitsOnly.take(5)
        }

        _uiState.update {
            it.copy(
                trainInput = input,
                trainNumber = resolvedNumber,
                matchingSuggestions = suggestions,
                errorMessage = null
            )
        }
    }

    fun onDateChange(date: String) {
        _uiState.update { it.copy(journeyDate = date, errorMessage = null) }
    }

    fun selectQuickTrain(number: String) {
        val train = TrainDirectory.findTrainByNumberOrName(number)
        val displayName = train?.name?.let { "$number • $it" } ?: number
        _uiState.update {
            it.copy(
                trainInput = displayName,
                trainNumber = number,
                matchingSuggestions = emptyList(),
                errorMessage = null
            )
        }
        trackTrain()
    }

    fun trackTrain() {
        val currentInput = _uiState.value.trainInput.trim()
        var number = _uiState.value.trainNumber.trim()

        if (number.length < 4) {
            val matchedTrain = TrainDirectory.findTrainByNumberOrName(currentInput)
            if (matchedTrain != null) {
                number = matchedTrain.number
                _uiState.update { it.copy(trainNumber = number) }
            }
        }

        if (number.length < 4 || !number.all { it.isDigit() }) {
            _uiState.update {
                it.copy(errorMessage = "Please enter a valid 5-digit train number (e.g. 12711) or select a train from the list.")
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                isColdStarting = false,
                errorMessage = null,
                errorType = null,
                matchingSuggestions = emptyList()
            )
        }

        // Trigger cold-start indicator after 4 seconds of waiting for Render
        coldStartJob?.cancel()
        coldStartJob = viewModelScope.launch {
            delay(4000)
            if (_uiState.value.isLoading) {
                _uiState.update { it.copy(isColdStarting = true) }
            }
        }

        viewModelScope.launch {
            val date = _uiState.value.journeyDate.ifBlank { null }
            val result = apiClient.fetchLiveStatus(trainNumber = number, date = date)

            coldStartJob?.cancel()

            when (result) {
                is LiveTrainResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isColdStarting = false,
                            liveStatus = result.data,
                            errorMessage = null,
                            errorType = null
                        )
                    }
                }
                is LiveTrainResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isColdStarting = false,
                            errorMessage = result.message,
                            errorType = result.errorType
                        )
                    }
                }
            }
        }
    }

    fun loadDemoStatus() {
        val number = if (_uiState.value.trainNumber.isNotBlank()) _uiState.value.trainNumber else "12711"
        val demo = LiveTrainApiClient.getDemoLiveStatus(number)
        _uiState.update {
            it.copy(
                isLoading = false,
                isColdStarting = false,
                liveStatus = demo,
                errorMessage = null,
                errorType = null,
                matchingSuggestions = emptyList()
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, errorType = null) }
    }
}
