package com.android.dicodingeventapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.dicodingeventapp.MainActivity
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.data.api.ApiConfig

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            val apiService = ApiConfig.getApiService()
            // PERBAIKAN: Panggil getEvents() dengan parameter yang sesuai (active = 1 untuk event aktif)
            val response = apiService.getEvents(active = 1)

            if (response.error || response.listEvents.isEmpty()) {
                return Result.success() // Jika ada error API atau tidak ada event, sukses saja tanpa notifikasi
            }

            val event = response.listEvents.firstOrNull()
                ?: return Result.success() // Seharusnya tidak null jika list tidak kosong

            // PERBAIKAN: Teruskan ID event jika Anda ingin membuka halaman detail event
            showNotification(event.name, event.beginTime, event.id)

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure() // Jika ada error di sini, kembalikan Result.failure()
        }
        return Result.success()
    }

    // Perbaiki tanda tangan fungsi untuk menerima eventId
    private fun showNotification(title: String, date: String, eventId: Int) {
        val channelId = "event_reminder_channel" // ID channel notifikasi
        val notificationId = 1 // ID unik untuk notifikasi
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Buat Intent untuk Activity yang akan dibuka saat notifikasi diklik
        // Asumsi MainActivity adalah tujuan. Jika ada DetailEventActivity, lebih baik ke sana.
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            // Jika Anda ingin membuka detail event tertentu, tambahkan data ke Intent
            // putExtra("EVENT_ID", eventId) // Anda bisa mengirim ID event
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // 2. Buat PendingIntent dari Intent
        // FLAG_UPDATE_CURRENT: Jika PendingIntent sudah ada, pertahankan dan ganti Intent yang ada
        // FLAG_IMMUTABLE: Rekomendasi Android 12+ untuk keamanan. Jika tidak mau, gunakan 0.
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0, // Request code, bisa unik jika banyak notifikasi
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Tambahkan FLAG_IMMUTABLE
        )

        // Buat Notification Channel (wajib untuk Android O/8.0 ke atas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Event Reminder", // Nama channel yang terlihat pengguna
                NotificationManager.IMPORTANCE_HIGH // Tingkat kepentingan (HIGH = notifikasi pop-up)
            ).apply {
                description = "Channel for daily event reminders"
            }
            manager.createNotificationChannel(channel)
        }

        // Bangun notifikasi
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Icon kecil di status bar (pastikan ada di res/drawable)
            .setContentTitle("Upcoming Event Reminder") // Judul notifikasi
            .setContentText("$title on $date") // Isi notifikasi
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioritas untuk pop-up di bawah Android O
            .setAutoCancel(true) // Notifikasi hilang saat diklik

            // 3. Set Content Intent ke notifikasi agar bisa diklik
            .setContentIntent(pendingIntent)

            .build()

        // Tampilkan notifikasi
        manager.notify(notificationId, notification)
    }
}