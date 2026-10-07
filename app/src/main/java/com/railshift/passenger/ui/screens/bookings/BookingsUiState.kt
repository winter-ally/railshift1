package com.railshift.passenger.ui.screens.bookings

import com.railshift.passenger.data.models.Trip

enum class BookingsTab {
    ACTIVE,
    PAST
}

data class BookingsUiState(
    val selectedTab: BookingsTab = BookingsTab.ACTIVE,
    val activeTrips: List<Trip> = emptyList(),
    val pastTrips: List<Trip> = emptyList(),
    val isLoading: Boolean = false
)
