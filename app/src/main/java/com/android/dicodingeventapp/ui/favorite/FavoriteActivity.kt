package com.android.dicodingeventapp.ui.favorite

import android.content.Intent
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
import com.android.dicodingeventapp.ui.detail.DetailActivity
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

        // Asumsikan 'activity_favorite.xml' memiliki Toolbar dengan ID 'toolbar'.
        setSupportActionBar(binding.toolbar)
        // Mengaktifkan tombol "Up" (panah kembali) di Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Menetapkan listener untuk tombol "Up" agar kembali ke Activity sebelumnya
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Kembali ke Activity sebelumnya
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = AppDatabase.getInstance(applicationContext)
        val repository = FavoriteRepository(db.favoriteDao())

        favoriteViewModel = ViewModelProvider(this, FavoriteViewModel.Factory(repository))
            .get(FavoriteViewModel::class.java)

        favoriteAdapter = FavoriteAdapter { event ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }
        binding.rvFavorites.layoutManager = LinearLayoutManager(this)
        binding.rvFavorites.adapter = favoriteAdapter

        favoriteViewModel.favorites.observe(this) { favorites ->
            favoriteAdapter.submitList(favorites)
            if (favorites.isEmpty()) {
                binding.tvNoFavorites.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
            } else {
                binding.tvNoFavorites.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE
            }
        }
    }
}
