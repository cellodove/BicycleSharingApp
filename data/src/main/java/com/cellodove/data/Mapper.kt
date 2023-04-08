package com.cellodove.data

import com.cellodove.data.model.NaverDrivingResponse
import com.cellodove.domain.data.FindRootResponse
import com.cellodove.domain.data.Path
import com.cellodove.domain.data.Route
import com.cellodove.domain.data.RouteUnitEnt

fun mapperToFindRootResponse(naverDrivingResponse : NaverDrivingResponse) : FindRootResponse {
    val mapperPath = naverDrivingResponse.route.routeUnitEnt.path.toList().map {
        Path(
            Pair(it.location.latitude,it.location.longitude)
        )
    }
    val mapperRouteUnitEnt = RouteUnitEnt(
        summary = naverDrivingResponse.route.routeUnitEnt.summary,
        path = mapperPath as ArrayList<Path>
    )
    val mapperRoute = Route(
        optionCode = naverDrivingResponse.route.optionCode,
        routeUnitEnt = mapperRouteUnitEnt
    )
    return FindRootResponse(
        code = naverDrivingResponse.code,
        messge = naverDrivingResponse.messge,
        currentDateTime = naverDrivingResponse.currentDateTime,
        route = mapperRoute
    )
}