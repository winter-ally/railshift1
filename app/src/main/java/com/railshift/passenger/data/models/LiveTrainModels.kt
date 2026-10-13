package com.railshift.passenger.data.models

data class LiveStation(
    val stationCode: String,
    val stationName: String,
    val arrivalTime: String? = null,
    val departureTime: String? = null,
    val actualArrivalTime: String? = null,
    val actualDepartureTime: String? = null,
    val delayMinutes: Int = 0,
    val hasDeparted: Boolean = false,
    val platformNumber: String? = null,
    val distanceKm: Int = 0
)

data class LiveTrainStatus(
    val trainNumber: String,
    val trainName: String,
    val runningDate: String,
    val statusSummary: String,
    val delayMinutes: Int = 0,
    val isRunningOnTime: Boolean = true,
    val currentStation: LiveStation? = null,
    val nextStation: LiveStation? = null,
    val sourceStation: LiveStation? = null,
    val destinationStation: LiveStation? = null,
    val lastUpdated: String? = null,
    val isCached: Boolean = false,
    val stations: List<LiveStation> = emptyList()
)

sealed class LiveTrainResult {
    data class Success(val data: LiveTrainStatus) : LiveTrainResult()
    data class Error(
        val errorType: LiveTrainErrorType,
        val message: String,
        val rawDetails: String? = null
    ) : LiveTrainResult()
}

enum class LiveTrainErrorType {
    INVALID_APP_KEY,
    TRAIN_NOT_FOUND,
    INVALID_TRAIN_NUMBER,
    SERVICE_UNAVAILABLE,
    TIMEOUT,
    NETWORK_ERROR,
    UNKNOWN
}
