package com.example.homespace.data.property;

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.homespace.GetPropertiesStartWithCountryQuery.Property
import com.example.homespace.data.PropertyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PropertyWithCountryPagingSource(
        private val propertyClient: PropertyClient,
        var query: String,
        private var initialPageNumber: Int
) : PagingSource<Int, Property>() {
        override fun getRefreshKey(state: PagingState<Int, Property>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                        val anchorPage = state.closestPageToPosition(anchorPosition)
                        anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
                }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Property> {
                val nextPageNumber = params.key ?: initialPageNumber
                val offset = if (nextPageNumber == 1) 0 else (nextPageNumber - 1) * 10
                return try {
                        withContext(Dispatchers.IO) {
                                var res = propertyClient.getPropertiesStartWithCountry(
                                        offset = offset,
                                        search = query
                                )
                                LoadResult.Page(
                                        data = res.data?.getPropertiesStartWithCountry?.properties!!,
                                        nextKey = if (res?.data?.getPropertiesStartWithCountry?.properties!!.isEmpty()) null else nextPageNumber + 1,
                                        prevKey = null,// if (nextPageNumber == DEFAULT_PAGE_SIZE) null else nextPageNumber - 1,
                                )

                        }
                } catch (e: Exception) {
                        LoadResult.Error(e)
                }
        }
}
