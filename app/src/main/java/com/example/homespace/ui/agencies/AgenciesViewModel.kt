package com.example.homespace.ui.agencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homespace.GetAgenciesQuery
// import com.example.homespace.GetPropertiesStartWithCountryQuery
import com.example.homespace.data.AgencyClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgenciesViewModel(private val agencyClient: AgencyClient) : ViewModel() {

    private val _agencies : MutableLiveData<List<GetAgenciesQuery.Agency>?> = MutableLiveData()
    val agencies: MutableLiveData<List<GetAgenciesQuery.Agency>?> = _agencies
    private val _couldGetAgencies : MutableLiveData<Boolean> = MutableLiveData(false)
    val couldGetAgencies: LiveData<Boolean> = _couldGetAgencies
    private val _count : MutableLiveData<Int> = MutableLiveData(0)
    val count: LiveData<Int> = _count

    fun getAgencies() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = agencyClient.getAgencies(/*searchString*/)
                if (!result.hasErrors && result.data != null) {
                    _agencies.postValue(result.data?.getAgencies?.agencies!!)
                    _count.postValue(result.data?.getAgencies?.count!!.toInt())
                    _couldGetAgencies.postValue(true)
                } else {
                    _agencies.postValue(null)
                    _couldGetAgencies.postValue(false)
                }
            }
        }
    }
}