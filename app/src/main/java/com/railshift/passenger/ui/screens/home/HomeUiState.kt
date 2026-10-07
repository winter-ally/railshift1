package com.railshift.passenger.ui.screens.home

import com.railshift.passenger.data.models.Trip
import com.railshift.passenger.data.models.UserProfile

data class HomeUiState(
    val userProfile: UserProfile? = null,
    val upcomingTrip: Trip? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
