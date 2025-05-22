package com.android.dicodingeventapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.android.dicodingeventapp.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
): ViewModel() {

    fun getUpcomingEvent() = liveData {
        val result = eventRepository.getUpcomingEvent()
        emit(result)
    }

    fun getFinishedEvent() = liveData {
        val result = eventRepository.getFinishedEvent()
        emit(result)
    }

    fun searchEvent(keyword : String) = liveData {
        val result = eventRepository.searchEvent(keyword)
        emit(result)
    }

    fun getEventDetail(id : Int) = liveData {
        val result = eventRepository.getEventDetail(id)
        emit(result)
    }

}