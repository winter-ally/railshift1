package com.railshift.passenger.ui.screens.reserved

import com.railshift.passenger.data.models.RecentSearch
import com.railshift.passenger.data.models.Station
import com.railshift.passenger.data.models.Train
import com.railshift.passenger.data.models.TrainClass

data class ReservedUiState(
    val fromStation: Station = Station("BZA", "Vijayawada Jn", "Vijayawada"),
    val toStation: Station = Station("MAS", "Chennai Central", "Chennai"),
    val journeyDateText: String = "Mon, 12 Oct 2026",
    val selectedDateChipIndex: Int = 2, // 0: Today, 1: Tomorrow, 2: Mon, 12 Oct
    val selectedClass: TrainClass = TrainClass.THREE_A,
    val showAvailableOnly: Boolean = true,
    val isFlexibleWithDates: Boolean = false,
    val recentSearches: List<RecentSearch> = emptyList(),
    val searchResults: List<Train> = emptyList(),
    val isSearching: Boolean = false,
    val bookedTripId: String? = null
)
