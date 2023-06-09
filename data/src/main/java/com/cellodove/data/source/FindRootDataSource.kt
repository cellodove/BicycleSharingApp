package com.cellodove.data.source

import android.util.Log
import com.cellodove.data.mapperToFindRootResponse
import com.cellodove.data.service.NaverService
import com.cellodove.domain.model.FindRootResponse
import com.cellodove.domain.model.Route
import com.cellodove.domain.model.RouteUnitEnt
import javax.inject.Inject

interface FindRootDataSource {
    suspend fun getRoot(start : String, goal : String) : FindRootResponse
}

class FindRootDataSourceImpl @Inject constructor(
    private val naverDrivingService: NaverService
) : FindRootDataSource {
    override suspend fun getRoot(start: String, goal: String): FindRootResponse {
        val response = naverDrivingService.getDrivingRoot(start, goal)
        Log.e("kkkk","kkkk DATA: $start /// $goal")
        return if (ResultCode.by(response.code) == ResultCode.SUCCESS){
            mapperToFindRootResponse(naverDrivingService.getDrivingRoot(start, goal))
        }else{
            arrayListOf<RouteUnitEnt>()
            val route = Route(
                traoptimal = listOf()
            )
            FindRootResponse(response.code,response.message,"",route)
        }
    }
}

enum class ResultCode(val value : String){
    SUCCESS("0"),
    START_GOAL_SAME("1"),
    START_OR_GOAL_IS_NOT_ROAD("2"),
    NOT_CAR_ROOT_RESULT("3"),
    STOPOVER_IS_NOT_AROUND_ROOD("4"),
    GOAL_IS_SO_FAR("5");
    companion object {
        fun by(value : String) = enumValues<ResultCode>().find { it.value == value } ?: ""
    }
}



