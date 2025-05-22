package com.android.dicodingeventapp.data.model

data class EventResponse(
    val error: Boolean,
    val message: String,
    val listEvents: List<Event>
)
