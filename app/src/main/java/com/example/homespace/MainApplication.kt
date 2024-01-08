package com.example.homespace

import android.app.Application
import com.example.homespace.di.appModule
import com.example.homespace.di.networkModule
import com.example.homespace.di.serviceModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Main Application
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin() {
            androidContext(this@MainApplication)
            androidLogger()
            modules(appModule, networkModule, serviceModule)
        }
    }
}