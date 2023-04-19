package com.cellodove.domain.usecase

import com.cellodove.domain.model.FindRootResponse
import com.cellodove.domain.repository.MapRepository
import javax.inject.Inject


interface FindRootUseCase {
    suspend fun getRootData(startPoint: String, endPoint: String): FindRootResponse
}

class FindRootUseCaseImpl @Inject constructor(private val mapRepository: MapRepository) :
    FindRootUseCase {
    override suspend fun getRootData(startPoint: String, endPoint: String): FindRootResponse {
        return mapRepository.findRoot(startPoint, endPoint)
    }
}