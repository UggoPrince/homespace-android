package com.example.homespace.di

import com.example.homespace.data.AgencyClient
import com.example.homespace.data.PropertyClient
import com.example.homespace.data.agency.AgencyRepository
import com.example.homespace.data.property.PropertyRepository
import com.example.homespace.data.property.PropertyWithCountryPagingSource
import com.example.homespace.data.property.SearchedPropertyPagingSource
import com.example.homespace.services.api.AgencyService
import com.example.homespace.services.api.PropertyService
import org.koin.dsl.module

val serviceModule = module {
    single { PropertyService(get()) }
    single { AgencyService(get()) }

    // clients
    single {PropertyClient(get())}
    single { AgencyClient(get()) }
    single {PropertyRepository(get())}
    single {AgencyRepository(get())}
}