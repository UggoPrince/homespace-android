package com.example.homespace.viewModels

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homespace.models.MyAddress
import com.example.homespace.services.LocationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    private val locationService = LocationService.instance
    private val _locationAccessGranted: MutableLiveData<Boolean> = MutableLiveData()
    val locationAccessGranted: LiveData<Boolean> get() = _locationAccessGranted

    private val _canGetLocation: MutableLiveData<Boolean> = MutableLiveData()
    val canGetLocation: LiveData<Boolean> get() = _canGetLocation

    private val _address: MutableLiveData<MyAddress> = MutableLiveData()
    val address: LiveData<MyAddress> get() = _address

    private val _addressList : MutableLiveData<List<Address>> = MutableLiveData()
    val addressList: LiveData<List<Address>> get() = _addressList

    fun updateLocationAccessGranted(value: Boolean) {
        _locationAccessGranted.postValue(value)
    }
    fun updateCanGetLocation(value: Boolean) {
        _canGetLocation.postValue(value)
    }

    private fun setAddress(value: MyAddress?) {
        _address.postValue(value!!)
    }

    fun setAddressList(value: List<Address>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _addressList.postValue(value)
            }
        }
    }

    fun getLocation(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                locationService.getDeviceLocation(context)
                    ?.addOnSuccessListener {
                        try {
                            if (it != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    locationService.geocoder.getFromLocation(
                                        it.latitude, it.longitude, 1
                                    ) { addressList ->
                                        Log.d("ADDRESS LIST", addressList.toString())
                                        val value = locationService.extractAddress(addressList)
                                        _address.postValue(value)
                                    }
                                } else {
                                    val data = locationService.geocoder.getFromLocation(
                                        it.latitude, it.longitude, 1)
                                    val value = locationService.extractAddress(data!!)
                                    _address.postValue(value)
                                }
                            } else _address.postValue(it)
                        } catch (e: Exception) {
                            Log.e("EXCEPTION", e.message.toString())
                        }
                }
                    ?.addOnFailureListener {
                        setAddress(null)
                    }
            }
        }
    }
}