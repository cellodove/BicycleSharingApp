package com.cellodove.data.model

import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLng

data class NaverDrivingResponse(
    @SerializedName("code") val code : String,
    @SerializedName("message") val message : String,
    @SerializedName("currentDateTime") val currentDateTime : String,
    @SerializedName("route") val route : Route
)

data class Route(
    @SerializedName("traoptimal") val traoptimal : List<RouteUnitEnt>
)

data class RouteUnitEnt(
    @SerializedName("summary") val summary : ResultDistance,
    @SerializedName("path") val path : List<List<Double>>
)

data class ResultDistance(
    @SerializedName("distance") val distance : String
)

