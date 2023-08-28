package com.example.homespace.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homespace.GetPropertiesQuery
import com.example.homespace.data.PropertyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.homespace.GetPropertiesStartWithCountryQuery

class HomeViewModel(private val propertyClient: PropertyClient) : ViewModel() {

    private val _properties :
            MutableLiveData<List<GetPropertiesStartWithCountryQuery.Property>> = MutableLiveData()
    val properties:
            LiveData<List<GetPropertiesStartWithCountryQuery.Property>> = _properties
    private val _searchedProperties :
            MutableLiveData<List<GetPropertiesQuery.Property>> = MutableLiveData()
    val searchedProperties:
            LiveData<List<GetPropertiesQuery.Property>> = _searchedProperties
    private val _couldGetProperties : MutableLiveData<Boolean> = MutableLiveData(false)
    val couldGetProperties: LiveData<Boolean> = _couldGetProperties
    private val _count : MutableLiveData<Int> = MutableLiveData(0)
    val count: LiveData<Int> = _count

    fun getHomeProperties(searchString: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = propertyClient.getPropertiesStartWithCountry(searchString)
                if (!result.hasErrors && result.data != null) {
                    _properties.postValue(result.data?.getPropertiesStartWithCountry?.properties!!)
                    _count.postValue(result.data?.getPropertiesStartWithCountry?.count!!)
                    _couldGetProperties.postValue(true)
                } else _couldGetProperties.postValue(false)
            }
        }
    }

    fun searchedProperties(searchString: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = propertyClient.getSearchedProperties(searchString)
                if (!result.hasErrors && result.data != null) {
                    _searchedProperties.postValue(result.data?.getProperties?.properties!!)
                    _count.postValue(result.data?.getProperties?.count!!)
                    _couldGetProperties.postValue(true)
                } else _couldGetProperties.postValue(false)
            }
        }
    }
}