package com.android.dicodingeventapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.dicodingeventapp.data.local.entity.FavoriteEventEntity
import com.android.dicodingeventapp.repository.FavoriteRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: FavoriteRepository) : ViewModel() {
    val favorites = repository.getAllFavorites()

    fun addFavorite(event: FavoriteEventEntity) = viewModelScope.launch {
        repository.addToFavorite(event)
    }

    fun removeFavorite(event: FavoriteEventEntity) = viewModelScope.launch {
        repository.removeFromFavorite(event)
    }

    // PENTING: Fungsi ini sekarang adalah suspend function karena repository.isFavorite adalah suspend
    suspend fun isFavorite(eventId: Int) = repository.isFavorite(eventId)

    // Factory untuk menginisialisasi ViewModel dengan Repository
    class Factory(private val repository: FavoriteRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavoriteViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
