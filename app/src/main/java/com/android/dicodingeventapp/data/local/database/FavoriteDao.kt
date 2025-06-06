package com.android.dicodingeventapp.data.local.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.dicodingeventapp.data.local.entity.FavoriteEventEntity


@Dao
interface FavoriteDao {
    // Menyimpan event favorit. Jika sudah ada (berdasarkan PrimaryKey), akan diganti.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(event: FavoriteEventEntity)

    // Menghapus event favorit dari database.
    @Delete
    suspend fun deleteFavorite(event: FavoriteEventEntity)

    // Mengambil semua event favorit. LiveData akan otomatis update UI.
    // PENTING: Pastikan nama tabel di sini sama dengan @Entity di FavoriteEventEntity (favorite_events)
    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): LiveData<List<FavoriteEventEntity>>

    // Memeriksa apakah event tertentu sudah ada di favorit.
    // PENTING: Pastikan nama tabel di sini sama dengan @Entity di FavoriteEventEntity (favorite_events)
    @Query("SELECT EXISTS(SELECT * FROM favorite_events WHERE id = :eventId)")
    suspend fun isFavorite(eventId: Int): Boolean

    // PENTING: Metode ini diperlukan oleh Repository dan ViewModel
    // untuk mendapatkan FavoriteEventEntity berdasarkan ID.
    @Query("SELECT * FROM favorite_events WHERE id = :id LIMIT 1")
    suspend fun getFavoriteById(id: Int): FavoriteEventEntity?
}
