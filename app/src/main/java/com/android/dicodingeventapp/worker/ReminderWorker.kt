package com.android.dicodingeventapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker // Import CoroutineWorker
import androidx.work.WorkerParameters // Import WorkerParameters
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.data.api.ApiConfig

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val apiService = ApiConfig.getApiService()
            // PERBAIKAN: Panggil getEvents() dengan parameter yang sesuai untuk event aktif/mendatang
            // Endpoint Anda: https://event-api.dicoding.dev/events?active=-1&limit=1
            // Di ApiService.kt, ada getEvents(@Query("active") active: Int = 1)
            // Jadi, panggil getEvents(active = 1) untuk event aktif.
            val response = apiService.getEvents(active = 1) // Mengambil event aktif/akan datang

            // Cek jika ada error dari API atau jika list event kosong
            if (response.error || response.listEvents.isEmpty()) {
                return Result.success() // Jika tidak ada event atau ada error, anggap sukses dan tidak tampilkan notifikasi
            }

            // Ambil event pertama dari list.
            // Asumsi event yang terdekat adalah event pertama dalam list 'active=1'.
            // Jika API tidak menjamin urutan, Anda mungkin perlu sorting sendiri.
            val event = response.listEvents.firstOrNull()
                ?: return Result.success() // Seharusnya tidak terjadi karena sudah dicek isEmpty()

            // PERBAIKAN: Gunakan properti yang benar dari Event.kt
            // nama event adalah 'name', tanggal mulai adalah 'beginTime'
            showNotification(event.name, event.beginTime)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure() // Mengindikasikan bahwa pekerjaan gagal jika ada exception jaringan/lainnya
        }
        return Result.success()
    }

    private fun showNotification(title: String, date: String) {
        val channelId = "event_reminder"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Pastikan drawable ic_notification ada
            .setContentTitle("Upcoming Event Reminder")
            .setContentText("$title on $date")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Event Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(1, notification)
    }
}