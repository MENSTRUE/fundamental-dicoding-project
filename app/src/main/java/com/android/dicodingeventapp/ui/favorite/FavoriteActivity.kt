package com.android.dicodingeventapp.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper // Import ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView // Import RecyclerView
import com.android.dicodingeventapp.data.local.database.AppDatabase
import com.android.dicodingeventapp.databinding.ActivityFavoriteBinding
import com.android.dicodingeventapp.repository.FavoriteRepository
import com.android.dicodingeventapp.ui.detail.DetailActivity
import com.android.dicodingeventapp.viewmodel.FavoriteViewModel
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur Toolbar sebagai ActionBar untuk Activity ini
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

        // Inisialisasi Adapter hanya dengan onClick listener (tanpa onDeleteClick karena menggunakan swipe)
        favoriteAdapter = FavoriteAdapter { event ->
            // Logika klik item untuk navigasi ke DetailActivity
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }
        binding.rvFavorites.layoutManager = LinearLayoutManager(this)
        binding.rvFavorites.adapter = favoriteAdapter

        favoriteViewModel.favorites.observe(this) { favorites ->
            favoriteAdapter.submitList(favorites)
            // Logika untuk menampilkan/menyembunyikan pesan "Tidak ada favorit"
            if (favorites.isEmpty()) {
                binding.tvNoFavorites.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
            } else {
                binding.tvNoFavorites.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE
            }
        }

        // MEMPERBAIKI: Implementasi Swipe-to-Delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, // Tidak ada dukungan untuk drag & drop
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Mendukung geser ke kiri dan kanan
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Tidak mengizinkan pemindahan item
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Dapatkan posisi item yang digeser
                val position = viewHolder.adapterPosition
                // Dapatkan event favorit dari adapter berdasarkan posisi
                val eventToDelete = favoriteAdapter.currentList[position]

                lifecycleScope.launch {
                    try {
                        // Panggil ViewModel untuk menghapus event dari database
                        favoriteViewModel.removeFavorite(eventToDelete)
                        Toast.makeText(this@FavoriteActivity, "${eventToDelete.title} dihapus dari favorit", Toast.LENGTH_SHORT).show()
                        // Karena LiveData Favorites akan mengupdate adapter secara otomatis,
                        // tidak perlu memanggil notifyItemRemoved() atau sejenisnya secara manual.
                        // RecyclerView akan diperbarui saat favorites.observe dipicu.
                    } catch (e: Exception) {
                        Toast.makeText(this@FavoriteActivity, "Gagal menghapus ${eventToDelete.title}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                        // Jika gagal, kembalikan item ke posisi semula (opsional, untuk UX lebih baik)
                        favoriteAdapter.notifyItemChanged(position)
                    }
                }
            }
        }

        // Lampirkan ItemTouchHelper ke RecyclerView
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvFavorites)
    }
}
