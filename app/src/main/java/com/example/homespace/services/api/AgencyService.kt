package com.example.homespace.services.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.Optional
import com.example.homespace.GetAgenciesQuery

class AgencyService(private val apolloClient: ApolloClient) {
    fun getAgencies(
        offset: Optional<Int?> = Optional.present(0),
        limit: Optional<Int?> = Optional.present(10)
    ): ApolloCall<GetAgenciesQuery.Data> {
        return apolloClient.query(GetAgenciesQuery(offset, limit))
    }
}