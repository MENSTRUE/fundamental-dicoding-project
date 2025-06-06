package com.android.dicodingeventapp.repository

import com.android.dicodingeventapp.data.local.database.FavoriteDao
import com.android.dicodingeventapp.data.local.entity.FavoriteEventEntity

class FavoriteRepository(private val favoriteDao: FavoriteDao) {
    fun getAllFavorites() = favoriteDao.getAllFavorites()

    suspend fun addToFavorite(event: FavoriteEventEntity) = favoriteDao.insertFavorite(event)

    suspend fun removeFromFavorite(event: FavoriteEventEntity) = favoriteDao.deleteFavorite(event)

    // PENTING: Menggunakan getFavoriteById dari DAO untuk memeriksa keberadaan
    // Ini adalah cara yang benar untuk memeriksa apakah event sudah difavoritkan
    suspend fun isFavorite(eventId: Int) = favoriteDao.getFavoriteById(eventId) != null
}
