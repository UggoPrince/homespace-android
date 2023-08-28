package com.example.homespace.services.api

import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.homespace.GetPropertiesQuery
import com.example.homespace.GetPropertiesStartWithCountryQuery

class PropertyService(private val apolloClient: ApolloClient) {
    fun getPropertiesStartWithCountry(
        offset: Optional<Int?> = Optional.present(0),
        limit: Optional<Int?> = Optional.present(10),
        search: Optional<String?> = Optional.present("")
    ): ApolloCall<GetPropertiesStartWithCountryQuery.Data> {
        return apolloClient.query(
            GetPropertiesStartWithCountryQuery(offset, limit, search)
        )
    }

    fun getProperties(
        offset: Optional<Int?> = Optional.present(0),
        limit: Optional<Int?> = Optional.present(10),
        search: Optional<String?> = Optional.present("")
    ): ApolloCall<GetPropertiesQuery.Data> {
        return apolloClient.query(GetPropertiesQuery(offset, limit, search))
    }
}