package org.otunjargych.tamtam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.api.ApiClient
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.model.Stations

class LocationViewModel : ViewModel() {

    private val _metroStationsList = MutableLiveData<Stations>()
    val metroStationsList: LiveData<Stations> = _metroStationsList


    fun getContent() {

        viewModelScope.launch {
            val retrofit = ApiClient().getRetrofit().create(ApiService::class.java)
            val response = retrofit.getMetroStations()
            if (response != null) {
                _metroStationsList.value = response.body()
            }
        }
    }

}

