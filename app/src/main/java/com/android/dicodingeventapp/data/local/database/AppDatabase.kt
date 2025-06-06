package com.android.dicodingeventapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration // PENTING: Pastikan import ini ada
import androidx.sqlite.db.SupportSQLiteDatabase // PENTING: Pastikan import ini ada
import com.android.dicodingeventapp.data.local.entity.FavoriteEventEntity

// PENTING: Tingkatkan versi database ke 2 atau lebih tinggi jika ini adalah perubahan pertama Anda
// Dan pastikan tableName di FavoriteEventEntity juga "favorite_events"
@Database(entities = [FavoriteEventEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // PENTING: Definisi objek Migration dari versi 1 ke 2
        // Ini akan menambahkan kolom 'logoUrl' dan jika Anda sebelumnya menggunakan "favorite_event"
        // di entity Anda dan AppDatabase, ini juga akan menangani perubahan nama tabel.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Perintah SQL untuk menambahkan kolom baru
                // PASTIKAN NAMA TABEL SAMA DENGAN @Entity(tableName = "...") di FavoriteEventEntity
                // dan juga sama dengan yang ada di FavoriteDao (yaitu "favorite_events")
                database.execSQL("ALTER TABLE favorite_events ADD COLUMN logoUrl TEXT NOT NULL DEFAULT ''")
                // Jika Anda juga mengubah nama tabel dari "favorite_event" ke "favorite_events",
                // Anda MUNGKIN perlu migrasi tambahan di sini (namun Room biasanya menangani ini secara internal
                // jika Entity diubah dan versi naik dengan migrasi kolom).
                // Contoh jika perlu rename: database.execSQL("ALTER TABLE favorite_event RENAME TO favorite_events");
                // Namun, kita sudah mengubah nama di @Entity dan @Query, jadi Room seharusnya menangani ini
                // selama properti "logoUrl" ditambahkan.
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_database" // Nama database Anda
                )
                    // PENTING: Hapus .fallbackToDestructiveMigration()
                    // Ganti dengan .addMigrations(MIGRATION_1_2) untuk migrasi yang aman
                    .addMigrations(MIGRATION_1_2) // Tambahkan objek migrasi yang telah Anda definisikan
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
