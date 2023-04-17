package com.cellodove.data.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cellodove.data.mapperToAddress
import com.cellodove.data.service.NaverService
import com.cellodove.domain.model.DomainAddresses
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


interface SearchAddressDataSource{
    fun getAddress(query:String) : PagingSource<Int, DomainAddresses>
}
const val SEARCH_STARTING_PAGE_INDEX = 1

class SearchAddressDataSourceImpl @Inject constructor(private val naverService: NaverService):SearchAddressDataSource{

    override fun getAddress(query:String): PagingSource<Int, DomainAddresses> {
        return object : PagingSource<Int, DomainAddresses>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DomainAddresses> {
                val position = params.key ?: SEARCH_STARTING_PAGE_INDEX
                return try {
                    val response = naverService.getAddress(query, position)
                    val addressResponse = response.addresses
                    val nextKey = if (response.addresses.isEmpty()) {
                        null
                    } else {
                        position + 1
                    }
                    LoadResult.Page(
                        data = mapperToAddress(addressResponse),
                        prevKey = if (position == SEARCH_STARTING_PAGE_INDEX) null else position - 1,
                        nextKey = nextKey
                    )
                } catch (exception: IOException) {
                    LoadResult.Error(exception)
                } catch (exception: HttpException) {
                    LoadResult.Error(exception)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, DomainAddresses>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }
        }
    }
}