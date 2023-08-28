package com.example.homespace.data

import com.apollographql.apollo3.network.http.HttpInfo
import com.example.homespace.GetAgenciesQuery
import com.example.homespace.models.ApiResponse
import com.example.homespace.services.api.AgencyService
import com.example.homespace.utils.GraphqlHttpResponse

class AgencyClient(private val agencyService: AgencyService) {
    suspend fun getAgencies(): ApiResponse<GetAgenciesQuery.Data> {
        var result = ApiResponse<GetAgenciesQuery.Data>()
        try {
            val res = agencyService.getAgencies().execute()
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