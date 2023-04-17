package com.cellodove.domain.repository

import androidx.paging.PagingData
import com.cellodove.domain.model.DomainAddresses
import com.cellodove.domain.model.FindRootResponse
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    suspend fun findRoot(startPoint : String, endPoint : String) : FindRootResponse

    fun searchAddressPaging(addressQuery : String) : Flow<PagingData<DomainAddresses>>
}