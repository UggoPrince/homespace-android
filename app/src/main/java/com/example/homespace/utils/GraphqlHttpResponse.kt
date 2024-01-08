package com.example.homespace.utils

import android.util.Log
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.api.Query
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.apollographql.apollo3.network.http.HttpInfo
import com.example.homespace.GetPropertiesStartWithCountryQuery
import com.example.homespace.GetAgenciesQuery
import com.example.homespace.models.ApiResponse
import okhttp3.ResponseBody.Companion.asResponseBody
import java.net.SocketTimeoutException

object GraphqlHttpResponse {
    private const val EXCEPTION = "exception"
    // handles the graphql success response
    /*@JvmName("success1")
    fun success(
        response: ApolloResponse<GetPropertiesStartWithCountryQuery.Data>
    ): ApiResponse<GetPropertiesStartWithCountryQuery.Data> {
        return ApiResponse(
            data = response.data,
            errors = response.errors,
            hasErrors = response.hasErrors(),
            statusCode = response.executionContext[HttpInfo]!!.statusCode
        )
    }

    @JvmName("success2")
    fun success(
        response: ApolloResponse<GetAgenciesQuery.Data>
    ): ApiResponse<GetAgenciesQuery.Data> {
        return ApiResponse(
            data = response.data,
            errors = response.errors,
            hasErrors = response.hasErrors(),
            statusCode = response.executionContext[HttpInfo]!!.statusCode
        )
    } */

    fun <T: Any> success(response: ApolloResponse<*>): ApiResponse<T> {
        return ApiResponse(
            data = response.data as T,
            errors = response.errors,
            hasErrors = response.hasErrors(),
            statusCode = response.executionContext[HttpInfo]!!.statusCode
        )
    }

    // handles the graphql http error responses
    fun <T: Any> failure(e: Exception): ApiResponse<T> {
        return when (e) {
            is ApolloHttpException -> {
                val body = e.body?.asResponseBody().toString()
                Log.e("ApolloHttpException", e.message.toString())
                ApiResponse(data = null, message = body, errorType = EXCEPTION, exception = e)
            }
            is ApolloNetworkException -> {
                val reason = e.message!!
                Log.e("ApolloNetworkException", e.message.toString())
                ApiResponse(data = null, message = reason, errorType = EXCEPTION, exception = e)
            }
            is SocketTimeoutException -> {
                Log.e("SocketTimeoutException", e.message.toString())
                ApiResponse(data = null, message = "Network timeout.", errorType = EXCEPTION, exception = e)
            }
            else -> {ApiResponse(data = null, exception = e)}
        }
    }
}