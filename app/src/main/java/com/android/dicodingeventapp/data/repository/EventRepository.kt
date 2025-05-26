package com.android.dicodingeventapp.data.repository

import com.android.dicodingeventapp.data.api.ApiService
import com.android.dicodingeventapp.data.model.Event

class EventRepository(private val apiService: ApiService) {

    suspend fun getUpcomingEvent(): List<Event> {
        return apiService.getEvents(active = 1).listEvents
    }

    suspend fun getFinishedEvent(): List<Event> {
        return apiService.getEvents(active = 0).listEvents
    }

    suspend fun searchEvent(keyword: String): List<Event> {
        return apiService.getEvents(active = -1, q = keyword).listEvents
    }

    suspend fun getEventDetail(id: Int): Event {
        val response = apiService.getEventDetail(id)
        return response.event
    }

    suspend fun getAllEvents(): List<Event> {
        return apiService.getEvents(active = -1).listEvents
    }

}
