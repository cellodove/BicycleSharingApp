package com.cellodove.data.source

import androidx.paging.PagingSource
import com.cellodove.data.model.NaverDrivingResponse

interface FindRootDataSource {
    fun getRoot(latitude : String, longitude : String) : NaverDrivingResponse
}


class FindRootDataSourceImpl{

}



