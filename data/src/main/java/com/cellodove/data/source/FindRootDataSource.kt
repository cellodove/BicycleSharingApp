package com.cellodove.data.source

import android.util.Log
import com.cellodove.data.mapperToFindRootResponse
import com.cellodove.data.service.NaverDrivingService
import com.cellodove.domain.data.FindRootResponse
import com.cellodove.domain.data.Path
import com.cellodove.domain.data.Route
import com.cellodove.domain.data.RouteUnitEnt
import javax.inject.Inject

interface FindRootDataSource {
    suspend fun getRoot(start : String, goal : String) : FindRootResponse
}

class FindRootDataSourceImpl @Inject constructor(
    private val naverDrivingService: NaverDrivingService
) : FindRootDataSource {
    override suspend fun getRoot(start: String, goal: String): FindRootResponse {
        val response = naverDrivingService.getDrivingRoot(start, goal)
        Log.e("kkkk","kkkk DATA: $start /// $goal")
        return if (response.code == "1"){
            mapperToFindRootResponse(naverDrivingService.getDrivingRoot(start, goal))
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



