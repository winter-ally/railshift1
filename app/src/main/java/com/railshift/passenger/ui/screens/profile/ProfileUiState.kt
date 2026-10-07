package com.railshift.passenger.ui.screens.profile

import com.railshift.passenger.data.models.ThemeMode
import com.railshift.passenger.data.models.UserProfile

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(
        name = "Ravi Kumar",
        email = "ravi.kumar@example.com",
        initials = "RK",
        walletBalance = 1240.00
    ),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isEditingName: Boolean = false,
    val isEditingEmail: Boolean = false,
    val isAddingMoney: Boolean = false,
    val isLoggedOut: Boolean = false
)
