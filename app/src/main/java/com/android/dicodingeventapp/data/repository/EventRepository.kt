package com.android.dicodingeventapp.data.repository

import com.android.dicodingeventapp.data.api.ApiService
import com.android.dicodingeventapp.data.model.Event

class EventRepository (private val apiService: ApiService){

    suspend fun getUpcomingEvent():List<Event> {
        return apiService.getActiveEvents()
    }

    suspend fun getFinishedEvent(): List<Event> {
        return apiService.getFinishedEvents()
    }

    suspend fun searchEvent(keyword: String): List<Event> {
        return  apiService.searchEvents(keyword = keyword)
    }

    suspend fun getEventDetail(id: Int): Event {
        return apiService.getEventDetail(id)
    }

}