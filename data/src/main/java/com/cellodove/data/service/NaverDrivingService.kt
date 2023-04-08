package com.cellodove.data.service

import com.cellodove.data.model.NaverDrivingResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NaverDrivingService {
    @GET("/map-direction/v1/driving?start={start}&goal={goal}")
    suspend fun getDrivingRoot(
        @Path("start") start : String,
        @Path("goal") goal : String
    ): NaverDrivingResponse
}