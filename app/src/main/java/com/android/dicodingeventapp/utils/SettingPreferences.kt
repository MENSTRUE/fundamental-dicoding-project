package com.android.dicodingeventapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// PERBAIKAN: Pastikan ini adalah satu-satunya deklarasi properti ekstensi dataStore untuk Context.
// Eror "Unresolved reference: datastore" di SettingsActivity.kt bisa terjadi jika ini tidak bisa dijangkau.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences(private val dataStore: DataStore<Preferences>) {

    private val DARK_THEME = booleanPreferencesKey("dark_theme")
    private val DAILY_REMINDER = booleanPreferencesKey("daily_reminder")

    // PERBAIKAN: Hapus fungsi getInstance().
    // Karena SettingPreferences sekarang diinisialisasi melalui konstruktor dengan DataStore,
    // dan Hilt akan mengelola penyediaan instance-nya.
    // Jika Anda masih ingin menggunakan getInstance() tanpa Hilt, Anda perlu mengimplementasikannya
    // dengan benar di sini (misalnya singleton pattern) yang menginisialisasi DataStore secara internal.
    // Namun, pendekatan yang lebih bersih untuk Dagger Hilt adalah melalui injeksi konstruktor.

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