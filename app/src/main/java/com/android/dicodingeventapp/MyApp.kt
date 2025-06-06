package com.android.dicodingeventapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.android.dicodingeventapp.utils.SettingPreferences // Pastikan ini diimpor
import com.android.dicodingeventapp.utils.dataStore // Pastikan ini diimpor
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class MyApp : Application() {

    // Buat CoroutineScope untuk Application (atau gunakan GlobalScope dengan hati-hati)
    // SupervisorJob() agar coroutine anak gagal tidak membatalkan coroutine induk
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        // Inisialisasi SettingPreferences menggunakan properti ekstensi dataStore dari Context
        val preferences = SettingPreferences(this.dataStore)

        // Luncurkan coroutine untuk membaca preferensi tema dari DataStore
        // dan menerapkan tema sebelum Activity apa pun dibuat.
        applicationScope.launch {
            val isDark = preferences.isDarkTheme().first() // Mengambil nilai tema pertama
            AppCompatDelegate.setDefaultNightMode(
                if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}