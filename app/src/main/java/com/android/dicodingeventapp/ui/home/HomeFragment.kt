package com.android.dicodingeventapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.dicodingeventapp.databinding.FragmentHomeBinding
import com.android.dicodingeventapp.ui.detail.DetailActivity
import com.android.dicodingeventapp.ui.viewmodel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()

    private lateinit var upcomingAdapter: UpcomingEventAdapter
    private lateinit var finishedAdapter: FinishedEventAdapter

    private var dataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        observeViewModel()

        if (!dataLoaded) {
            viewModel.loadUpcomingEvent()
            viewModel.loadFinishedEvent() // ini deklarasi
            dataLoaded = true
        }
    }


    private fun setupAdapters() {
        upcomingAdapter = UpcomingEventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }

        finishedAdapter = FinishedEventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }

        // Tampilkan shimmer loading awal
        upcomingAdapter.isLoading = true
        finishedAdapter.isLoading = true

        binding.recyclerViewSlider.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            upcomingAdapter.isLoading = false
            upcomingAdapter.submitList(events ?: emptyList())
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedAdapter.isLoading = false
            finishedAdapter.submitList(events ?: emptyList())
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                upcomingAdapter.isLoading = false
                finishedAdapter.isLoading = false
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

//    private fun observeViewModel() {
//        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
//            Log.d("HomeFragment", "upcomingEvents updated: ${events?.size ?: 0} items")
//            upcomingAdapter.isLoading = false
//            upcomingAdapter.submitList(events ?: emptyList())
//        }
//
//        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
//            Log.d("HomeFragment", "finishedEvents updated: ${events?.size ?: 0} items")
//            finishedAdapter.isLoading = false
//            finishedAdapter.submitList(events ?: emptyList())
//        }
//
//        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
//            Log.d("HomeFragment", "errorMessage received: $message")  // <- ini ditaruh di sini
//            if (!message.isNullOrEmpty()) {
//                upcomingAdapter.isLoading = false
//                finishedAdapter.isLoading = false
//                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
