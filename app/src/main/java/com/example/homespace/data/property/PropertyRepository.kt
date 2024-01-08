package com.example.homespace.data.property

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.homespace.GetPropertiesQuery
import com.example.homespace.GetPropertiesStartWithCountryQuery
import com.example.homespace.data.PropertyClient
import kotlinx.coroutines.flow.Flow

class PropertyRepository(private val propertyClient: PropertyClient) {
    fun getPropertyByCountryFlow(pagingConfig: PagingConfig = this.getDefaultPageConfig(),
                                     query: String, initialPageNumber: Int):
            Flow<PagingData<GetPropertiesStartWithCountryQuery.Property>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { PropertyWithCountryPagingSource(propertyClient, query, initialPageNumber) }
        ).flow
    }

    fun getSearchedPropertiesFlow(pagingConfig: PagingConfig = getDefaultPageConfig(),
                                      query: String, initialPageNumber: Int):
            Flow<PagingData<GetPropertiesQuery.Property>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { SearchedPropertyPagingSource(propertyClient, query, initialPageNumber) }
        ).flow
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 1, enablePlaceholders = true)
    }
}