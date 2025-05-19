package com.android.dicodingeventapp.data.api

import com.android.dicodingeventapp.data.model.Event
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // mengambil event yang aktif (akan datang)
    @GET("events")
    suspend fun getActiveEvents(
        @Query("active") active: Int = 1
    ): List<Event>

    // mengambil event yang sudah selesai
    @GET("events")
    suspend fun getFinishedEvents(
        @Query("active") active: Int = 0
    ): List<Event>

    //mengambil event berdasarkan kata kunci (keyword)
    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") keyword: String
    ): List<Event>

    //mengambil detail event berdasarkan id
    @GET("events")
    suspend fun getEventDetail(
        @Path("id") id: Int
    ): Event

}