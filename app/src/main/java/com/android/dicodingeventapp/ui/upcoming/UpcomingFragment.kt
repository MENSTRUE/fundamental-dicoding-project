package com.android.dicodingeventapp.ui.upcoming

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.dicodingeventapp.databinding.FragmentUpcomingBinding
import com.android.dicodingeventapp.ui.detail.DetailActivity
import com.android.dicodingeventapp.ui.viewmodel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()

    private lateinit var adapter: UpcomingEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        observeUpcomingEvents()
        observeLoadingState()
        observeErrorMessages()

        viewModel.loadUpcomingEvent()
    }

    private fun setupAdapter() {
        adapter = UpcomingEventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }

        binding.recyclerViewUpcoming.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUpcoming.adapter = adapter
        adapter.isLoading = true // tampilkan shimmer di awal
        adapter.submitList(emptyList())
    }

    private fun observeUpcomingEvents() {
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }
    }

    private fun observeLoadingState() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            adapter.isLoading = isLoading
        }
    }

    private fun observeErrorMessages() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (!message.isNullOrEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
