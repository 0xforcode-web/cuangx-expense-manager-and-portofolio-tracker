package com.cuangx.finance.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private val PASSCODE_ENABLED = booleanPreferencesKey("passcode_enabled")
        private val PASSCODE_VALUE = stringPreferencesKey("passcode_value")
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val START_DAY = intPreferencesKey("start_day")
        private val DEFAULT_CURRENCY = stringPreferencesKey("default_currency")
        private val DARK_MODE = stringPreferencesKey("dark_mode")
    }

    val passcodeEnabled: Flow<Boolean> = context.dataStore.data.map { it[PASSCODE_ENABLED] ?: false }
    val passcodeValue: Flow<String> = context.dataStore.data.map { it[PASSCODE_VALUE] ?: "" }
    val biometricEnabled: Flow<Boolean> = context.dataStore.data.map { it[BIOMETRIC_ENABLED] ?: false }
    val startDay: Flow<Int> = context.dataStore.data.map { it[START_DAY] ?: 1 }
    val defaultCurrency: Flow<String> = context.dataStore.data.map { it[DEFAULT_CURRENCY] ?: "IDR" }
    val darkMode: Flow<String> = context.dataStore.data.map { it[DARK_MODE] ?: "system" }

    suspend fun setPasscodeEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PASSCODE_ENABLED] = enabled }
    }

    suspend fun setPasscodeValue(passcode: String) {
        context.dataStore.edit { it[PASSCODE_VALUE] = passcode }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setStartDay(day: Int) {
        context.dataStore.edit { it[START_DAY] = day }
    }

    suspend fun setDefaultCurrency(currency: String) {
        context.dataStore.edit { it[DEFAULT_CURRENCY] = currency }
    }

    suspend fun setDarkMode(mode: String) {
        context.dataStore.edit { it[DARK_MODE] = mode }
    }
}
