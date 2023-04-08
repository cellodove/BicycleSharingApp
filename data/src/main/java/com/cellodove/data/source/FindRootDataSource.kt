package com.cellodove.data.source

import com.cellodove.data.mapperToFindRootResponse
import com.cellodove.data.service.NaverDrivingService
import com.cellodove.domain.data.FindRootResponse
import javax.inject.Inject

interface FindRootDataSource {
    suspend fun getRoot(latitude : String, longitude : String) : FindRootResponse
}

class FindRootDataSourceImpl@Inject constructor(
    private val naverDrivingService: NaverDrivingService
) : FindRootDataSource {
    override suspend fun getRoot(latitude: String, longitude: String): FindRootResponse {
        return mapperToFindRootResponse(naverDrivingService.getDrivingRoot(latitude, longitude))
    }
}



