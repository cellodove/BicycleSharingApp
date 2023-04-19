package com.cellodove.domain.usecase

import androidx.paging.PagingData
import com.cellodove.domain.model.DomainAddresses
import com.cellodove.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface SearchAddressUseCase {
    fun getAddressPagingData(addressQuery: String): Flow<PagingData<DomainAddresses>>
}

class SearchAddressUseCaseImpl @Inject constructor(private val mapRepository: MapRepository) :
    SearchAddressUseCase {
    override fun getAddressPagingData(addressQuery: String): Flow<PagingData<DomainAddresses>> {
        return mapRepository.searchAddressPaging(addressQuery)
    }
}