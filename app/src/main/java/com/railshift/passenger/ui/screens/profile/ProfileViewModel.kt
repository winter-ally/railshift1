package com.railshift.passenger.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.railshift.passenger.data.models.ThemeMode
import com.railshift.passenger.data.preferences.PreferencesRepository
import com.railshift.passenger.data.repository.RailshiftRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: RailshiftRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getUserProfile().collect { profile ->
                _uiState.update { it.copy(userProfile = profile) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.themeModeFlow.collect { theme ->
                _uiState.update { it.copy(themeMode = theme) }
            }
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            repository.updateProfile(name, email)
        }
    }

    fun addMoney(amount: Double) {
        viewModelScope.launch {
            repository.addWalletMoney(amount)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val nextTheme = when (_uiState.value.themeMode) {
                ThemeMode.LIGHT -> ThemeMode.DARK
                ThemeMode.DARK -> ThemeMode.LIGHT
                ThemeMode.SYSTEM -> ThemeMode.DARK
            }
            preferencesRepository.setThemeMode(nextTheme)
        }
    }

    fun logout() {
        _uiState.update { it.copy(isLoggedOut = true) }
    }
}
