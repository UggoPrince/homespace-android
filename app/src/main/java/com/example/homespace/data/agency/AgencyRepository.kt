package com.example.homespace.data.agency

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.homespace.GetAgenciesQuery
import com.example.homespace.data.AgencyClient
import kotlinx.coroutines.flow.Flow

class AgencyRepository(private val agencyClient: AgencyClient) {
    fun getAgenciesFlow(pagingConfig: PagingConfig = getDefaultPageConfig(),
                        initialPageNumber: Int):
            Flow<PagingData<GetAgenciesQuery.Agency>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { AgencyPagingSource(agencyClient, initialPageNumber) }
        ).flow
    }
    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 1, enablePlaceholders = true)
    }
}