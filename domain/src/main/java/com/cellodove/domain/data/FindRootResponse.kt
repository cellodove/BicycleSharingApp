package com.cellodove.domain.data

import com.naver.maps.geometry.LatLng

data class FindRootResponse(
    val code : String,
    val messge : String,
    val currentDateTime : String,
    val route : Route
)

data class Route(
    val optionCode : String,
    val routeUnitEnt : RouteUnitEnt
)

data class RouteUnitEnt(
    val summary : String,
    val path : ArrayList<Path>
)

data class Path(
    val location : LatLng
)