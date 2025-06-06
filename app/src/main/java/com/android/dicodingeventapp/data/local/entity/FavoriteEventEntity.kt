package com.android.dicodingeventapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Pastikan nama tabel ini konsisten dengan AppDatabase dan FavoriteDao
// PENTING: Jika sebelumnya "favorite_event", pastikan Anda telah menjalankan migrasi ke "favorite_events"
@Entity(tableName = "favorite_events")
data class FavoriteEventEntity(
    @PrimaryKey val id: Int, // id event unik sebagai primary key
    val title: String,
    val description: String,
    val startDate: String,
    val location: String,
    val imageUrl: String, // Untuk gambar cover (mediaCover)
    val logoUrl: String // Untuk gambar logo (imageLogo) - Ini yang baru ditambahkan
)
