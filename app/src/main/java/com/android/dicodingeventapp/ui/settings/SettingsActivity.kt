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
import com.android.dicodingeventapp.utils.dataStore // Import yang diperlukan untuk properti ekstensi Context.dataStore
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

        // Handle tombol back di toolbar
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inisialisasi SettingPreferences menggunakan properti ekstensi dataStore
        preferences = SettingPreferences(applicationContext.dataStore)

        // Observe current theme dan set listener dengan aman untuk menghindari looping
        lifecycleScope.launch {
            val isDark = preferences.isDarkTheme().first()

            // 1. Nonaktifkan listener sementara sebelum mengatur status switch secara programatis.
            binding.switchTheme.setOnCheckedChangeListener(null)
            // 2. Atur status switch berdasarkan preferensi yang tersimpan.
            binding.switchTheme.isChecked = isDark
            // 3. Aktifkan kembali listener setelah status diatur.
            binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
                lifecycleScope.launch {
                    preferences.setDarkTheme(isChecked)
                    applyTheme(isChecked)
                }
            }
            // 4. Terapkan tema awal saat Activity pertama kali dibuat/dimulai.
            applyTheme(isDark)
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
        // PERBAIKAN: Ubah 1 menjadi 1L agar sesuai dengan tipe Long
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