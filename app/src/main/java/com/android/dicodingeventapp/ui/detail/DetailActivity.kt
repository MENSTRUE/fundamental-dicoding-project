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
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.data.model.Event
import com.android.dicodingeventapp.databinding.ActivityDetailBinding
import com.android.dicodingeventapp.ui.viewmodel.EventViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel: EventViewModel by viewModels()

    private var currentEvent: Event? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DetailActivity", "onCreate dipanggil")

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getIntExtra("EVENT_ID", -1)
        if (eventId == -1) {
            Log.e("DetailActivity", "Event ID tidak ditemukan!")
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE // tampilkan saat loading

        // Observe LiveData detail event untuk update UI
        viewModel.getEventDetail(eventId).observe(this) { event: Event ->
            binding.progressBar.visibility = View.GONE // sembunyikan saat data sudah ada
            event?.let {
                currentEvent = it
                binding.apply {
                    // Set image cover dan logo
                    Glide.with(this@DetailActivity)
                        .load(it.mediaCover) // sesuaikan nama properti
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.eror_image)
                        .into(imgCover)

                    Glide.with(this@DetailActivity)
                        .load(it.imageLogo)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.eror_image)
                        .into(imgLogo)

                    tvEventName.text = it.name
                    tvSummary.text = it.summary ?: "" // kalau ada summary
                    tvDate.text =
                        "${it.beginTime} - ${it.endTime}" // bisa disesuaikan dari properti tanggal
                    tvLocation.text = it.cityName ?: ""
                    tvOrganizer.text = it.ownerName ?: ""
                    tvCategory.text = it.category ?: ""
                    tvDescription.text =
                        HtmlCompat.fromHtml(it.description ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)

                }
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

    }
}
