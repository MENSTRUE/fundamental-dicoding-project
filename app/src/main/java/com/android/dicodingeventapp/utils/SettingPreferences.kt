package com.android.dicodingeventapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore // Import ini
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Properti ekstensi untuk mengakses DataStore dari Context, harus di luar kelas
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences(private val dataStore: DataStore<Preferences>) {

    private val DARK_THEME = booleanPreferencesKey("dark_theme")
    private val DAILY_REMINDER = booleanPreferencesKey("daily_reminder")

    fun isDarkTheme(): Flow<Boolean> = dataStore.data
        .map { it[DARK_THEME] ?: false }

    suspend fun setDarkTheme(value: Boolean) {
        dataStore.edit { it[DARK_THEME] = value }
    }

    fun isReminderActive(): Flow<Boolean> = dataStore.data
        .map { it[DAILY_REMINDER] ?: false }

    suspend fun setReminderActive(value: Boolean) {
        dataStore.edit { it[DAILY_REMINDER] = value }
    }
}