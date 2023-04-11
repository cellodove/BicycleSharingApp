package com.cellodove.domain.repository

import androidx.paging.PagingData
import com.cellodove.domain.data.DomainAddresses
import com.cellodove.domain.data.FindRootResponse
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    suspend fun findRoot(startPoint : String, endPoint : String) : FindRootResponse

    fun searchAddressPaging(addressQuery : String) : Flow<PagingData<DomainAddresses>>
}