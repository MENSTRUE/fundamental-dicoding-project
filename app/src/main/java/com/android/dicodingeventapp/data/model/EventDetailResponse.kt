package com.android.dicodingeventapp.data.model

data class EventDetailResponse(
    val error: Boolean,
    val message: String,
    val event: Event
)