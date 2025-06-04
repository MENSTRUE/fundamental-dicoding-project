package com.android.dicodingeventapp.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.android.dicodingeventapp.databinding.ActivitySettingsBinding
import com.android.dicodingeventapp.utils.SettingPreferences
import com.android.dicodingeventapp.utils.dataStore
import com.android.dicodingeventapp.worker.ReminderWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferences: SettingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Asumsi ini akan di-inject oleh Hilt di versi final,
        // namun jika belum sepenuhnya terintegrasi, ini bisa jadi placeholder.
        // Jika Hilt sudah terintegrasi, Anda bisa menggunakan @Inject lateinit var preferences: SettingPreferences
        // dan menghapus baris inisialisasi manual ini.
        preferences =
            com.android.dicodingeventapp.utils.SettingPreferences(applicationContext.dataStore) // PERBAIKAN: Gunakan constructor SettingPreferences yang baru

        // Observe current theme
        lifecycleScope.launch {
            val isDark = preferences.isDarkTheme().first()
            binding.switchTheme.isChecked = isDark
            applyTheme(isDark)
        }

        // Handle toggle theme
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                preferences.setDarkTheme(isChecked)
                applyTheme(isChecked)
            }
        }

        // Observe reminder
        lifecycleScope.launch {
            val isActive = preferences.isReminderActive().first()
            binding.switchReminder.isChecked = isActive
            handleReminder(isActive)
        }

        // Handle toggle reminder
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                preferences.setReminderActive(isChecked)
                handleReminder(isChecked)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun handleReminder(isActive: Boolean) {
        val workManager = WorkManager.getInstance(this)
        // PERBAIKAN: Ubah 1 menjadi 1L
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1L, TimeUnit.DAYS).build()
        if (isActive) {
            workManager.enqueueUniquePeriodicWork(
                "daily_reminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        } else {
            workManager.cancelUniqueWork("daily_reminder")
        }
    }
}