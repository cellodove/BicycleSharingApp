package com.cellodove.data

import com.cellodove.data.model.Addresses
import com.cellodove.data.model.NaverDrivingResponse
import com.cellodove.data.model.NaverSearchAddressResponse
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

fun mapperToSearchAddressResponse(naverSearchAddressResponse : NaverSearchAddressResponse) : SearchAddressResponse {
    return SearchAddressResponse(
        status = naverSearchAddressResponse.status,
        errorMessage = naverSearchAddressResponse.errorMessage,
        meta = AddressMeta(
            totalCount = naverSearchAddressResponse.meta.totalCount,
            page = naverSearchAddressResponse.meta.page,
            count = naverSearchAddressResponse.meta.count
        ),
        addresses = naverSearchAddressResponse.addresses.map {
            DomainAddresses(
                roadAddress = it.roadAddress,
                jibunAddress = it.jibunAddress,
                x = it.x,
                y = it.y
            )
        }
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