package com.android.dicodingeventapp.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.dicodingeventapp.databinding.FragmentSearchBinding
import com.android.dicodingeventapp.ui.detail.DetailActivity
import com.android.dicodingeventapp.ui.viewmodel.EventViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()
    private lateinit var adapter: SearchEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        observeAllEvents()
        observeSearchResults()

        // Load all events awalnya supaya tampil semua
        viewModel.loadAllEvents()

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                handleSearchQuery(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handleSearchQuery(newText)
                return true
            }
        })

        binding.searchBar.setOnClickListener {
            binding.searchBar.isIconified = false
            binding.searchBar.requestFocus()
        }
    }

    private fun setupAdapter() {
        adapter = SearchEventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }
        binding.rvsearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvsearch.adapter = adapter

        adapter.isLoading = true
        adapter.submitList(emptyList())
    }

    private fun handleSearchQuery(query: String?) {
        adapter.isLoading = true
        adapter.submitList(emptyList())

        if (query.isNullOrEmpty()) {
            viewModel.loadAllEvents()
        } else {
            viewModel.searchEvent(query)
        }
    }

    private fun observeAllEvents() {
        viewModel.allEvents.observe(viewLifecycleOwner) { events ->
            adapter.isLoading = false
            adapter.submitList(events)
        }
    }

    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.isLoading = false
            adapter.submitList(results)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
