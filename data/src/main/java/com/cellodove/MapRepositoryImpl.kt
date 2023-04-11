package com.cellodove

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cellodove.data.source.FindRootDataSource
import com.cellodove.data.source.SearchAddressDataSource
import com.cellodove.domain.data.DomainAddresses
import com.cellodove.domain.data.FindRootResponse
import com.cellodove.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MapRepositoryImpl@Inject constructor(
    private val findRootDataSource : FindRootDataSource,
    private val searchAddressDataSource: SearchAddressDataSource
) : MapRepository {

    override suspend fun findRoot(startPoint: String, endPoint: String): FindRootResponse {
        return findRootDataSource.getRoot(startPoint,endPoint)
    }

    override fun searchAddressPaging(addressQuery: String): Flow<PagingData<DomainAddresses>> {
        return Pager(config = PagingConfig(pageSize = NETWORK_PAGE_SIZE , enablePlaceholders = false),
        pagingSourceFactory = {searchAddressDataSource.getAddress(addressQuery)}).flow
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 10
    }
}