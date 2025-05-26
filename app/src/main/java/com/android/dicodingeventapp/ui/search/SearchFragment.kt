package com.android.dicodingeventapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.databinding.FragmentSearchBinding
import com.android.dicodingeventapp.ui.detail.DetailActivity
import com.android.dicodingeventapp.ui.viewmodel.EventViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tampilkan semua event awal
        showEvents(null)

        // Setup listener untuk searchBar (SearchView)
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                showEvents(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                showEvents(newText)
                return true
            }
        })

        // Klik untuk expand searchBar
        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
            binding.searchBar.requestFocus()
        }
    }

    private fun showEvents(keyword: String?) {
        // Clear dulu layout sebelumnya
        binding.linearLayoutSearch.removeAllViews()

        val liveData = if (keyword.isNullOrEmpty()) {
            viewModel.getAllEvents()
        } else {
            viewModel.searchEvent(keyword)
        }

        liveData.observe(viewLifecycleOwner) { events ->
            Log.d("SearchFragment", "Events count: ${events?.size ?: 0}")
            events?.forEach { event ->
                val itemView = layoutInflater.inflate(
                    R.layout.item_finishing_event,
                    binding.linearLayoutSearch,
                    false
                )

                val ivEvent = itemView.findViewById<ImageView>(R.id.ivEvent)
                val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
                val tvCategory = itemView.findViewById<TextView>(R.id.tv_event_category)
                val tvBeginTime = itemView.findViewById<TextView>(R.id.tvBeginTime)
                val tvEndTime = itemView.findViewById<TextView>(R.id.tvEndTime)
                val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
                val tvOwner = itemView.findViewById<TextView>(R.id.tvOwner)

                tvTitle.text = event.name
                tvCategory.text = event.category
                tvBeginTime.text = event.beginTime
                tvEndTime.text = event.endTime
                tvLocation.text = event.cityName
                tvOwner.text = event.ownerName

                Glide.with(itemView.context)
                    .load(event.imageLogo)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.eror_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivEvent)

                itemView.setOnClickListener {
                    val intent = Intent(requireContext(), DetailActivity::class.java)
                    intent.putExtra("EVENT_ID", event.id)
                    Log.d("SearchFragment", "Kirim event ID: ${event.id}")
                    startActivity(intent)
                }

                binding.linearLayoutSearch.addView(itemView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
