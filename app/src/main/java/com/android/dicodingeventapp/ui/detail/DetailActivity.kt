package com.android.dicodingeventapp.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.data.local.database.AppDatabase
import com.android.dicodingeventapp.data.local.entity.FavoriteEventEntity
import com.android.dicodingeventapp.data.model.Event
import com.android.dicodingeventapp.databinding.ActivityDetailBinding
import com.android.dicodingeventapp.repository.FavoriteRepository
import com.android.dicodingeventapp.ui.viewmodel.EventViewModel
import com.android.dicodingeventapp.viewmodel.FavoriteViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: EventViewModel by viewModels()
    private var currentEvent: Event? = null

    private lateinit var favoriteViewModel: FavoriteViewModel
    // Variabel ini akan diupdate setiap kali checkFavoriteStatus dipanggil,
    // atau setelah operasi add/remove favorit berhasil.
    private var isEventCurrentlyFavorite: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DetailActivity", "onCreate dipanggil")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val eventId = intent.getIntExtra("EVENT_ID", -1)

        val db = AppDatabase.getInstance(applicationContext)
        val favoriteRepository = FavoriteRepository(db.favoriteDao())
        favoriteViewModel = ViewModelProvider(this, FavoriteViewModel.Factory(favoriteRepository))
            .get(FavoriteViewModel::class.java)

        if (eventId == -1) {
            Log.e("DetailActivity", "Event ID tidak ditemukan!")
            Toast.makeText(this, "Informasi event tidak tersedia.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        viewModel.loadEventDetail(eventId)

        viewModel.eventDetail.observe(this) { event: Event? ->
            binding.progressBar.visibility = View.GONE
            event?.let {
                currentEvent = it
                Log.d("DetailActivity", "Event detail loaded: ${it.name}")

                binding.apply {
                    Glide.with(this@DetailActivity)
                        .load(it.mediaCover)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.eror_image)
                        .into(imgCover)

                    Glide.with(this@DetailActivity)
                        .load(it.imageLogo)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.eror_image)
                        .into(imgLogo)

                    val remainingQuota = it.quota - it.registrants

                    tvEventName.text = it.name
                    tvSummary.text = it.summary ?: ""
                    tvDate.text = "${it.beginTime} - ${it.endTime}"
                    tvLocation.text = it.cityName ?: ""
                    tvOrganizer.text = it.ownerName ?: ""
                    tvCategory.text = it.category ?: ""
                    tvDescription.text = HtmlCompat.fromHtml(it.description ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
                    tvQuota.text = remainingQuota.toString()
                }

                // Panggil fungsi ini untuk memeriksa status favorit awal
                checkFavoriteStatus(it.id)

            } ?: run {
                Log.e("DetailActivity", "Event with ID $eventId not found or failed to load.")
                Toast.makeText(this, "Gagal memuat detail event.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.btnRegister.setOnClickListener {
            val link = currentEvent?.link
            if (!link.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Link pendaftaran tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        binding.imageButtonFavorite.setOnClickListener {
            currentEvent?.let { event ->
                val favoriteEvent = FavoriteEventEntity(
                    id = event.id,
                    title = event.name,
                    description = event.description ?: "",
                    startDate = "${event.beginTime} - ${event.endTime}",
                    location = event.cityName ?: "",
                    imageUrl = event.mediaCover ?: "",
                    logoUrl = event.imageLogo ?: ""
                )

                lifecycleScope.launch {
                    try {
                        // PENTING: Periksa status favorit TERBARU dari database
                        val isCurrentlyFavoriteInDbAtClick = favoriteViewModel.isFavorite(event.id)
                        Log.d("DetailActivity", "Status favorit sebelum klik: $isCurrentlyFavoriteInDbAtClick")

                        if (isCurrentlyFavoriteInDbAtClick) {
                            favoriteViewModel.removeFavorite(favoriteEvent)
                            // Update state dan UI ikon secara langsung SETELAH operasi berhasil
                            isEventCurrentlyFavorite = false
                            updateFavoriteButtonIcon(isEventCurrentlyFavorite)
                            Toast.makeText(this@DetailActivity, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
                            Log.d("DetailActivity", "Event dihapus dari favorit: ${event.name}")
                        } else {
                            favoriteViewModel.addFavorite(favoriteEvent)
                            // Update state dan UI ikon secara langsung SETELAH operasi berhasil
                            isEventCurrentlyFavorite = true
                            updateFavoriteButtonIcon(isEventCurrentlyFavorite)
                            Toast.makeText(this@DetailActivity, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                            Log.d("DetailActivity", "Event ditambahkan ke favorit: ${event.name}")
                        }
                        // Panggilan ini sekarang lebih berfungsi sebagai validasi akhir/sinkronisasi
                        // atau untuk menangani kasus yang sangat jarang terjadi
                        // checkFavoriteStatus(event.id) // Bisa dihapus atau tetap ada, tergantung kebutuhan
                        // untuk jaminan sinkronisasi ekstra.
                        // Untuk respon cepat, kita sudah update di atas.

                    } catch (e: Exception) {
                        Log.e("DetailActivity", "Error during favorite toggle: ${e.message}", e)
                        Toast.makeText(this@DetailActivity, "Gagal mengubah status favorit.", Toast.LENGTH_SHORT).show()
                        // Jika ada error, periksa kembali status dari DB untuk menampilkan ikon yang akurat
                        checkFavoriteStatus(event.id)
                    }
                }
            } ?: run {
                Toast.makeText(this, "Data event belum sepenuhnya dimuat.", Toast.LENGTH_SHORT).show()
                Log.w("DetailActivity", "Attempted to toggle favorite but currentEvent is null.")
            }
        }
    }

    private fun checkFavoriteStatus(eventId: Int) {
        lifecycleScope.launch {
            Log.d("DetailActivity", "Memeriksa status favorit untuk event ID: $eventId")
            try {
                val isCurrentlyFavoriteInDb = favoriteViewModel.isFavorite(eventId)
                isEventCurrentlyFavorite = isCurrentlyFavoriteInDb // Perbarui state boolean
                updateFavoriteButtonIcon(isCurrentlyFavoriteInDb) // Perbarui UI ikon
                Log.d("DetailActivity", "Status favorit untuk event ID $eventId: $isEventCurrentlyFavorite")
            } catch (e: Exception) {
                Log.e("DetailActivity", "Error checking favorite status: ${e.message}", e)
                Toast.makeText(this@DetailActivity, "Gagal memeriksa status favorit.", Toast.LENGTH_SHORT).show()
                updateFavoriteButtonIcon(false) // Atur ikon ke status default jika ada error
            }
        }
    }

    private fun updateFavoriteButtonIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.imageButtonFavorite.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            binding.imageButtonFavorite.setImageResource(R.drawable.ic_favorite_not_filled)
        }
    }
}
