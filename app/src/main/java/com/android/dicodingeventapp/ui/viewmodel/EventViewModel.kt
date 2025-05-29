package com.android.dicodingeventapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dicodingeventapp.data.model.Event
import com.android.dicodingeventapp.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage


    private val _upcomingEvents = MutableLiveData<List<Event>>()
    val upcomingEvents: LiveData<List<Event>> get() = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<Event>>()
    val finishedEvents: LiveData<List<Event>> get() = _finishedEvents

    private val _eventDetail = MutableLiveData<Event>()
    val eventDetail: LiveData<Event> get() = _eventDetail

    private val _searchResults = MutableLiveData<List<Event>>()
    val searchResults: LiveData<List<Event>> get() = _searchResults

    private val _allEvents = MutableLiveData<List<Event>>()
    val allEvents: LiveData<List<Event>> get() = _allEvents

    fun loadUpcomingEvent() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = eventRepository.getUpcomingEvent()
                _upcomingEvents.value = result
                _errorMessage.value = null // clear error
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error loading upcoming events", e)
                _errorMessage.value = "Gagal memuat event yang akan datang. Coba lagi nanti."
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun loadFinishedEvent() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = eventRepository.getFinishedEvent()
                _finishedEvents.value = result
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error loading finished events", e)
                _errorMessage.value = "Gagal memuat event yang telah selesai."
            } finally {
                _isLoading.value = false
            }
        }
    }



    fun loadEventDetail(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = eventRepository.getEventDetail(id)
                _eventDetail.value = result
                _errorMessage.value = null
                Log.d("EventViewModel", "Detail event ID $id = $result")
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error loading event detail", e)
                _errorMessage.value = "Gagal memuat detail event."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchEvent(keyword: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = eventRepository.searchEvent(keyword)
                _searchResults.value = result
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error searching event", e)
                _errorMessage.value = "Gagal melakukan pencarian. Periksa koneksi internet."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAllEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = eventRepository.getAllEvents()
                _allEvents.value = result
            } catch (e: Exception) {
                Log.e("EventViewModel", "Error loading all events", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


}
