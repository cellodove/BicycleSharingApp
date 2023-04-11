package com.cellodove.domain.usecase

import androidx.paging.PagingData
import com.cellodove.domain.data.DomainAddresses
import com.cellodove.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow

class SearchAddressUseCase(private val mapRepository: MapRepository) {
    fun getAddressPagingData(addressQuery : String) : Flow<PagingData<DomainAddresses>> {
        return mapRepository.searchAddressPaging(addressQuery)
    }
}