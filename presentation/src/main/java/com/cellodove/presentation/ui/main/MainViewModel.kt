package com.cellodove.presentation.ui.main

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cellodove.domain.data.FindRootResponse
import com.cellodove.domain.usecase.FindRootUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val findRootUseCase : FindRootUseCase) : ViewModel() {

    private val _findRootData = MutableLiveData<FindRootResponse>()
    val findRootData : LiveData<FindRootResponse> = _findRootData

    fun getAddress(lat: Double, lng: Double, context : Context): String {
        val geoCoder = Geocoder(context, Locale.KOREA)
        val address: ArrayList<Address>
        var addressResult = "주소를 가져 올 수 없습니다."
        try {
            //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
            //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
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
}