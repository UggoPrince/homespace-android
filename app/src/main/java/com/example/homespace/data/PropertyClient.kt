package com.example.homespace.data

import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.http.HttpInfo
import com.example.homespace.GetPropertiesStartWithCountryQuery
import com.example.homespace.GetPropertiesQuery
import com.example.homespace.models.ApiResponse
import com.example.homespace.services.api.PropertyService
import com.example.homespace.utils.GraphqlHttpResponse.failure
import com.example.homespace.utils.GraphqlHttpResponse.success

class PropertyClient (
    private val propertyService: PropertyService
    ) {
    suspend fun getPropertiesStartWithCountry(search: String):
            ApiResponse<GetPropertiesStartWithCountryQuery.Data> {
        var result = ApiResponse<GetPropertiesStartWithCountryQuery.Data>()
        try {
            val res =
                propertyService
                    .getPropertiesStartWithCountry(
                        Optional.present(0),
                        Optional.present(0),
                        Optional.present(search)
                    )
                    .execute() as ApolloResponse<*>
            val status = res.executionContext[HttpInfo]?.statusCode
            if (status in 200 .. 299) {
                result = success(res)
            }
        } catch (e: Exception) {
            result = failure(e)
        }
        return result
    }

    suspend fun getSearchedProperties(search: String):
            ApiResponse<GetPropertiesQuery.Data> {
        var result = ApiResponse<GetPropertiesQuery.Data>()
        try {
            val res =
                propertyService
                    .getProperties(
                        Optional.present(0),
                        Optional.present(0),
                        Optional.present(search)
                    )
                    .execute()
            val status = res.executionContext[HttpInfo]?.statusCode
            if (status in 200 .. 299) {
                result = success(res)
            }
        } catch (e: Exception) {
            result = failure(e)
        }
        return result
    }
}