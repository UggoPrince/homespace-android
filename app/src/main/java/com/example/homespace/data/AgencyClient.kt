package com.example.homespace.data

import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.http.HttpInfo
import com.example.homespace.GetAgenciesQuery
import com.example.homespace.models.ApiResponse
import com.example.homespace.services.api.AgencyService
import com.example.homespace.utils.GraphqlHttpResponse

class AgencyClient(private val agencyService: AgencyService) {
    suspend fun getAgencies(offset: Int): ApiResponse<GetAgenciesQuery.Data> {
        var result = ApiResponse<GetAgenciesQuery.Data>()
        try {
            val res = agencyService.getAgencies(
                Optional.present(offset),
                Optional.present(10),
            ).execute()
            val status = res.executionContext[HttpInfo]?.statusCode
            if (status in 200 .. 299) {
                result = GraphqlHttpResponse.success(res)
            }
        } catch (e: Exception) {
            result = GraphqlHttpResponse.failure(e)
        }
        return result
    }
}