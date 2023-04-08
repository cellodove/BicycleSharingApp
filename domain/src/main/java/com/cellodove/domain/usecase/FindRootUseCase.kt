package com.cellodove.domain.usecase

import com.cellodove.domain.data.FindRootResponse
import com.cellodove.domain.repository.MapRepository

class FindRootUseCase(private val mapRepository: MapRepository) {
    suspend fun getRootData(startPoint: String, endPoint : String): FindRootResponse {
        return mapRepository.findRoot(startPoint,endPoint)
    }
}