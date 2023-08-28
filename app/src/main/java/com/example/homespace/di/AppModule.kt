package com.example.homespace.di

import com.example.homespace.ui.agencies.AgenciesViewModel
import com.example.homespace.ui.home.HomeViewModel
import com.example.homespace.viewModels.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::MainViewModel)
    viewModel{ HomeViewModel(get()) }
    viewModel{AgenciesViewModel(get())}
}