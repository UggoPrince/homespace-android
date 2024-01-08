package com.example.homespace.services

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.homespace.models.MyAddress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import java.util.*


class LocationService private constructor() {
    var locationAccessGranted: Boolean = false
    var locationEnabled: Boolean = false
    var networkEnabled: Boolean = false
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var geocoder: Geocoder
    var address: MyAddress? = null

    fun canGetLocation(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    ACCESS_COARSE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun locationAccessIsGranted(permissions:  Map<String, @JvmSuppressWildcards Boolean>): Boolean {
        locationAccessGranted = when {
            permissions[ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                true
            }
            permissions[ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                true
            } else -> {
                // No location access granted.
                false
            }
        }
        return locationAccessGranted
    }

    fun setFusedLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun setGeocoder(context: Context) {
        geocoder = Geocoder(context, Locale.getDefault())
    }

    /*fun setAddress(a: Address) {
        address = a
    }*/

    fun extractAddress(locData: List<Address>): MyAddress {
        val data = locData[0]
        return MyAddress(
            data.adminArea,
            data.subAdminArea,
            data.locality,
            data.thoroughfare,
            data.countryName,
            data.getAddressLine(0))
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(context: Context): Task<Location>? {
        return if (canGetLocation(context)/* && location == null*/) {
            val priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
            val cancellationTokenSource = CancellationTokenSource()
            return fusedLocationClient.getCurrentLocation(
                priority, cancellationTokenSource.token,
            )
        } else {
            null
        }
    }

    fun destroy() {
        ourInstance = null
    }

    companion object {
        private var ourInstance: LocationService? = null

        val instance: LocationService
            get() {
                if (ourInstance == null)
                    ourInstance = LocationService()
                return ourInstance!!
            }
    }
}