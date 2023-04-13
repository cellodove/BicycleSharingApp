package com.cellodove.presentation.ui.main

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cellodove.domain.data.DomainAddresses
import com.cellodove.domain.data.FindRootResponse
import com.cellodove.domain.usecase.BicyclesLocationUseCase
import com.cellodove.domain.usecase.FindRootUseCase
import com.cellodove.domain.usecase.SearchAddressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val findRootUseCase : FindRootUseCase,
    private val searchAddressUseCase : SearchAddressUseCase,
    private val bicyclesLocationUseCase : BicyclesLocationUseCase
) : ViewModel() {

    private val _findRootData = MutableLiveData<FindRootResponse>()
    val findRootData : LiveData<FindRootResponse> = _findRootData

    private val _getBicyclesLocationData = MutableLiveData<List<List<Double>>>()
    val getBicyclesLocationData : LiveData<List<List<Double>>> = _getBicyclesLocationData

    var startPoint = Pair(0.0,0.0)
    var endPoint = Pair(0.0,0.0)

    fun getAddress(lat: Double, lng: Double, context : Context): String {
        val geoCoder = Geocoder(context, Locale.KOREA)
        val address: ArrayList<Address>
        var addressResult = "주소를 가져 올 수 없습니다."
        try {
            address = geoCoder.getFromLocation(lat, lng, 1) as ArrayList<Address>
            if (address.size > 0) {
                // 주소 받아오기
                val currentLocationAddress = address[0].getAddressLine(0)
                    .toString()
                addressResult = currentLocationAddress

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return addressResult
    }

    fun getFindRoot(startPoint : String, endPoint : String){
        viewModelScope.launch {
            _findRootData.value = findRootUseCase.getRootData(startPoint,endPoint)
        }
    }

    fun searchAddress(query: String): Flow<PagingData<DomainAddresses>> {
        return searchAddressUseCase.getAddressPagingData(query).cachedIn(viewModelScope)
    }

    fun getBicyclesLocation(x:Double, y:Double){
        _getBicyclesLocationData.value = bicyclesLocationUseCase.getRandomLocation(x,y)
    }
}