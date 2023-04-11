package com.cellodove.data.service

import com.cellodove.data.model.NaverDrivingResponse
import com.cellodove.data.model.NaverSearchAddressResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverService {
    @GET("/map-direction/v1/driving")
    suspend fun getDrivingRoot(@Query("start", encoded = true) start : String, @Query("goal", encoded = true) goal : String): NaverDrivingResponse

    @GET("/map-geocode/v2/geocode")
    suspend fun getAddress(
        @Query(value = "query") query : String,
        @Query(value = "page") page: Int,
    ) : NaverSearchAddressResponse
}