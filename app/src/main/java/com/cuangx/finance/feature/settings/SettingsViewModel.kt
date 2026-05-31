package com.cuangx.finance.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuangx.finance.core.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val passcodeEnabled: Boolean = false,
    val biometricEnabled: Boolean = false,
    val startDay: Int = 1,
    val defaultCurrency: String = "IDR",
    val darkMode: String = "system"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferences.passcodeEnabled,
        userPreferences.biometricEnabled,
        userPreferences.startDay,
        userPreferences.defaultCurrency,
        userPreferences.darkMode
    ) { passcode, biometric, startDay, currency, darkMode ->
        SettingsUiState(
            passcodeEnabled = passcode,
            biometricEnabled = biometric,
            startDay = startDay,
            defaultCurrency = currency,
            darkMode = darkMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setPasscodeEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setPasscodeEnabled(enabled) }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setBiometricEnabled(enabled) }
    }

    fun setStartDay(day: Int) {
        viewModelScope.launch { userPreferences.setStartDay(day) }
    }

    fun setDefaultCurrency(currency: String) {
        viewModelScope.launch { userPreferences.setDefaultCurrency(currency) }
    }

    fun setDarkMode(mode: String) {
        viewModelScope.launch { userPreferences.setDarkMode(mode) }
    }
}
