package com.cellodove.data.source

import com.cellodove.data.mapperToFindRootResponse
import com.cellodove.data.service.NaverDrivingService
import com.cellodove.domain.data.FindRootResponse
import com.cellodove.domain.data.Path
import com.cellodove.domain.data.Route
import com.cellodove.domain.data.RouteUnitEnt
import javax.inject.Inject

interface FindRootDataSource {
    suspend fun getRoot(latitude : String, longitude : String) : FindRootResponse
}

class FindRootDataSourceImpl @Inject constructor(
    private val naverDrivingService: NaverDrivingService
) : FindRootDataSource {
    override suspend fun getRoot(latitude: String, longitude: String): FindRootResponse {
        val response = naverDrivingService.getDrivingRoot(latitude, longitude)
        return if (response.code == "1"){
            mapperToFindRootResponse(naverDrivingService.getDrivingRoot(latitude, longitude))
        }else{
            val mapperPath = arrayListOf<Path>()

            val mapperRouteUnitEnt = RouteUnitEnt(
                summary = "",
                path = mapperPath
            )
            val route = Route(
                optionCode = "",
                routeUnitEnt = mapperRouteUnitEnt
            )
            FindRootResponse("","","",route)
        }
    }
}



