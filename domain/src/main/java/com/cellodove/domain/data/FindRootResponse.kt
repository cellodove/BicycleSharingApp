package com.cellodove.domain.data

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
    val location : Pair<Double,Double>
)