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

    //    fun loadWorkData() {
//        val list: MutableList<Note> = ArrayList()
//        _work.postValue(State.Loading())
//        viewModelScope.launch {
//            delay(1500)
//            mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_WORKS)
//            mRefAds.valuesEventFlow().collect { result ->
//                when (result) {
//                    is EventResponse.Changed -> {
//                        val snapshot = result.snapshot
//                        for (dataSnapshot: DataSnapshot in snapshot.children) {
//                            var note = dataSnapshot.getValue(Note::class.java)!!
//                            list.add(0, note)
//                            _work.postValue(State.Success(list))
//                        }
//
//                    }
//                    is EventResponse.Cancelled -> {
//                        _work.postValue(State.Error())
//                        val exception = result.error
//                    }
//                }
//            }
//        }
//    }

}

