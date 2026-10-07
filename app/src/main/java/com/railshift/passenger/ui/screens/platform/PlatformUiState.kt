package com.railshift.passenger.ui.screens.platform

import com.railshift.passenger.data.models.Station

data class PlatformUiState(
    val journeyDateText: String = "7 Oct 2026 (Today)",
    val selectedStation: Station? = null,
    val recentStations: List<Station> = listOf(
        Station("BZA", "Vijayawada Jn", "Vijayawada"),
        Station("MAS", "Chennai Central", "Chennai"),
        Station("HYB", "Hyderabad", "Hyderabad"),
        Station("SC", "Secunderabad", "Secunderabad")
    ),
    val isBooking: Boolean = false,
    val bookedTicketId: String? = null
)
