package com.example.homespace.data.agency

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.homespace.GetAgenciesQuery.Agency
import com.example.homespace.data.AgencyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AgencyPagingSource(
    private val agencyClient: AgencyClient,
    private var initialPageNumber: Int,
) : PagingSource<Int, Agency>() {
    override fun getRefreshKey(state: PagingState<Int, Agency>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>):
            LoadResult<Int, Agency> {
        val nextPageNumber = params.key ?: initialPageNumber
        val offset = if (nextPageNumber == 1) 0 else (nextPageNumber - 1) * 10
        return try {
            withContext(Dispatchers.IO) {
                var res = agencyClient.getAgencies(
                    offset = offset,
                )

                LoadResult.Page(
                    data = res.data?.getAgencies?.agencies!!,
                    nextKey = if (res.data?.getAgencies?.agencies!!.isEmpty()) null else nextPageNumber + 1,
                    prevKey = null,// if (nextPageNumber == 1) null else nextPageNumber - 1,
                )
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}