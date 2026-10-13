package com.railshift.passenger.ui.screens.services

import com.railshift.passenger.data.models.LiveTrainErrorType
import com.railshift.passenger.data.models.LiveTrainStatus
import com.railshift.passenger.data.models.Train
import java.text.SimpleDateFormat
import java.util.*

data class TrackTrainUiState(
    val trainNumber: String = "12711",
    val trainInput: String = "12711",
    val journeyDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val isLoading: Boolean = false,
    val isColdStarting: Boolean = false,
    val liveStatus: LiveTrainStatus? = null,
    val errorMessage: String? = null,
    val errorType: LiveTrainErrorType? = null,
    val isConfiguredWithKey: Boolean = true,
    val matchingSuggestions: List<Train> = emptyList(),
    val quickSuggestions: List<Pair<String, String>> = listOf(
        "12711" to "Pinakini SF",
        "12727" to "Godavari Exp",
        "12704" to "Falaknuma",
        "20833" to "Vande Bharat",
        "12603" to "Hyderabad Exp"
    )
)
