package com.cellodove.data

import com.cellodove.data.model.Addresses
import com.cellodove.data.model.NaverDrivingResponse
import com.cellodove.data.model.NaverSearchAddressResponse
import com.cellodove.domain.model.*

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

fun mapperToAddress(addresses: List<Addresses>) : List<DomainAddresses> {
    return addresses.toList().map {
        DomainAddresses(
            roadAddress = it.roadAddress,
            jibunAddress = it.jibunAddress,
            x = it.x,
            y = it.y
        )
    }
}