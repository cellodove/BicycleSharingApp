package com.cellodove.data.model

import com.google.gson.annotations.SerializedName
import com.naver.maps.geometry.LatLng

data class NaverDrivingResponse(
    @SerializedName("code") val code : String,
    @SerializedName("messge") val messge : String,
    @SerializedName("currentDateTime") val currentDateTime : String,
    @SerializedName("route") val route : Route
)

data class Route(
    @SerializedName("optionCode") val optionCode : String,
    @SerializedName("RouteUnitEnt") val routeUnitEnt : RouteUnitEnt
)

data class RouteUnitEnt(
    @SerializedName("summary") val summary : String,
    @SerializedName("path") val path : ArrayList<Path>
)

data class Path(
    @SerializedName("location") val location : LatLng
)

