package com.cellodove.domain.model

data class FindRootResponse(
    val code : String,
    val message : String,
    val currentDateTime : String,
    val route : Route
)

data class Route(
    val traoptimal : List<RouteUnitEnt>
)

data class RouteUnitEnt(
    val summary : ResultDistance,
    val path : List<List<Double>>
)

data class ResultDistance(
    val distance : String
)