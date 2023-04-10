package com.cellodove.data

import com.cellodove.data.model.NaverDrivingResponse
import com.cellodove.domain.data.*

fun mapperToFindRootResponse(naverDrivingResponse : NaverDrivingResponse) : FindRootResponse {

    val mapperRoute = Route(
        traoptimal = naverDrivingResponse.route.traoptimal.map {
            RouteUnitEnt(
                summary = ResultDistance(it.summary.distance),
                path = it.path
            )
        }
    )

    return FindRootResponse(
        code = naverDrivingResponse.code,
        message = naverDrivingResponse.message,
        currentDateTime = naverDrivingResponse.currentDateTime,
        route = mapperRoute
    )
}