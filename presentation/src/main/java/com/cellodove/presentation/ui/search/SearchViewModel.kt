package com.cellodove.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cellodove.domain.data.DomainAddresses
import com.cellodove.domain.usecase.SearchAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchAddressUseCase : SearchAddressUseCase) : ViewModel() {
    fun searchAddress(query: String): Flow<PagingData<DomainAddresses>> {
        return searchAddressUseCase.getAddressPagingData(query).cachedIn(viewModelScope)
    }
}