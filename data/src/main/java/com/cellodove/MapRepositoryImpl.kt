package com.cellodove

import com.cellodove.data.source.FindRootDataSource
import com.cellodove.domain.data.FindRootResponse
import com.cellodove.domain.repository.MapRepository
import javax.inject.Inject

class MapRepositoryImpl@Inject constructor(
    private val findRootDataSource : FindRootDataSource
) : MapRepository {

    override suspend fun findRoot(startPoint: String, endPoint: String): FindRootResponse {
        return findRootDataSource.getRoot(startPoint,endPoint)
    }
}