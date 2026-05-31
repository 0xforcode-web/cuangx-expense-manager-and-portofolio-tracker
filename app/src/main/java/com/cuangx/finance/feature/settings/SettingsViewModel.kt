package com.cuangx.finance.feature.settings

import com.cuangx.finance.core.util.BackupManager
import java.io.File
import android.net.Uri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    val passcodeValue: String = "",
    val biometricEnabled: Boolean = false,
    val startDay: Int = 1,
    val defaultCurrency: String = "IDR",
    val darkMode: String = "system"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _event = MutableSharedFlow<SettingsEvent>()
    val event = _event.asSharedFlow()

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferences.passcodeEnabled,
        userPreferences.passcodeValue,
        userPreferences.biometricEnabled,
        userPreferences.startDay,
        userPreferences.defaultCurrency,
        userPreferences.darkMode
    ) { flows ->
        SettingsUiState(
            passcodeEnabled = flows[0] as Boolean,
            passcodeValue = flows[1] as String,
            biometricEnabled = flows[2] as Boolean,
            startDay = flows[3] as Int,
            defaultCurrency = flows[4] as String,
            darkMode = flows[5] as String
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setPasscodeEnabled(enabled: Boolean) {
        viewModelScope.launch { userPreferences.setPasscodeEnabled(enabled) }
    }

    fun setPasscodeValue(passcode: String) {
        viewModelScope.launch { userPreferences.setPasscodeValue(passcode) }
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

    fun backupData() {
        viewModelScope.launch {
            try {
                val file = backupManager.exportBackup()
                _event.emit(SettingsEvent.BackupSuccess(file))
            } catch (e: Exception) {
                _event.emit(SettingsEvent.ShowError(e.message ?: "Gagal membuat backup"))
            }
        }
    }

    fun restoreData(uri: Uri) {
        viewModelScope.launch {
            try {
                backupManager.restoreBackup(uri)
                _event.emit(SettingsEvent.RestoreSuccess)
            } catch (e: Exception) {
                _event.emit(SettingsEvent.ShowError(e.message ?: "Gagal restore data"))
            }
        }
    }
}

sealed class SettingsEvent {
    data class BackupSuccess(val file: File) : SettingsEvent()
    data object RestoreSuccess : SettingsEvent()
    data class ShowError(val message: String) : SettingsEvent()
}
