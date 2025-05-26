package com.android.dicodingeventapp.data.api

import com.android.dicodingeventapp.data.model.EventDetailResponse
import com.android.dicodingeventapp.data.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Ambil list events dengan filter active:
    // active = 1 -> event aktif/akan datang (default)
    // active = 0 -> event selesai
    // active = -1 -> semua event
    // Bisa juga pakai query 'q' untuk search dan 'limit' untuk jumlah maksimal data
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int = 1,
        @Query("q") q: String? = null
    ): EventResponse


    // Ambil detail event berdasarkan ID
    @GET("events/{id}")
    suspend fun getEventDetail(
        @Path("id") id: Int
    ): EventDetailResponse
}
