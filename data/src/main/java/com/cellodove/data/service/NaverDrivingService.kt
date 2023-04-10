package com.cellodove.data.service

import com.cellodove.data.model.NaverDrivingResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NaverDrivingService {
    @GET("/map-direction/v1/driving")
    suspend fun getDrivingRoot(@Query("start", encoded = true) start : String, @Query("goal", encoded = true) goal : String): NaverDrivingResponse
}