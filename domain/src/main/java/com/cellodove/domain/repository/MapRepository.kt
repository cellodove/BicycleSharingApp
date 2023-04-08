package com.cellodove.domain.repository

import com.cellodove.domain.data.FindRootResponse

interface MapRepository {
    suspend fun findRoot(startPoint : String, endPoint : String) : FindRootResponse
}