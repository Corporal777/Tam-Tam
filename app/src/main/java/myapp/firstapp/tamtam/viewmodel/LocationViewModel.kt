package myapp.firstapp.tamtam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import myapp.firstapp.tamtam.api.ApiClient
import myapp.firstapp.tamtam.api.ApiService
import myapp.firstapp.tamtam.model.Stations

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

