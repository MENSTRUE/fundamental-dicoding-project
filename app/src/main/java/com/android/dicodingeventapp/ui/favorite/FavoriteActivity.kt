package com.android.dicodingeventapp.ui.favorite

import android.content.Intent // Import Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.dicodingeventapp.data.local.database.AppDatabase
import com.android.dicodingeventapp.databinding.ActivityFavoriteBinding
import com.android.dicodingeventapp.repository.FavoriteRepository
import com.android.dicodingeventapp.ui.detail.DetailActivity // Import DetailActivity
import com.android.dicodingeventapp.viewmodel.FavoriteViewModel

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = AppDatabase.getInstance(applicationContext)
        val repository = FavoriteRepository(db.favoriteDao())

        favoriteViewModel = ViewModelProvider(this, FavoriteViewModel.Factory(repository))
            .get(FavoriteViewModel::class.java)

        // MEMPERBAIKI: Implementasi logika klik item untuk navigasi ke DetailActivity
        favoriteAdapter = FavoriteAdapter { event ->
            // Membuat Intent untuk membuka DetailActivity
            val intent = Intent(this, DetailActivity::class.java)
            // Menambahkan ID event sebagai extra ke Intent
            intent.putExtra("EVENT_ID", event.id)
            // Memulai DetailActivity
            startActivity(intent)
        }
        binding.rvFavorites.layoutManager = LinearLayoutManager(this)
        binding.rvFavorites.adapter = favoriteAdapter

        favoriteViewModel.favorites.observe(this) { favorites ->
            favoriteAdapter.submitList(favorites)
            // Logika untuk menampilkan/menyembunyikan pesan "Tidak ada favorit"
            if (favorites.isEmpty()) {
                binding.tvNoFavorites.visibility = View.VISIBLE // Asumsikan tvNoFavorites exists
                binding.rvFavorites.visibility = View.GONE
            } else {
                binding.tvNoFavorites.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE
            }
        }
    }
}