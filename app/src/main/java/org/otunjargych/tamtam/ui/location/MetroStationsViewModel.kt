package org.otunjargych.tamtam.ui.location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.location.LocationRepository
import org.otunjargych.tamtam.model.request.MetroStationsModel
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain

class MetroStationsViewModel(
    private val appData: AppData,
    private val locationRepository: LocationRepository
) : BaseViewModel() {

    private val listStations = arrayListOf<MetroStationsModel>()
    private val listSelectedStations = arrayListOf<String>()

    private val _metroStations = MutableLiveData<List<MetroStationsModel>?>()
    val metroStations: LiveData<List<MetroStationsModel>?> get() = _metroStations

    private val _selectedStations = MutableLiveData<List<String>>(listSelectedStations)
    val selectedStations: LiveData<List<String>> get() = _selectedStations


    fun loadMetroStations(city: String?, list: List<String>) {
        listSelectedStations.addAll(list)
        compositeDisposable += locationRepository.getMetroStations(city)
            .doOnSuccess { listStations.addAll(it) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _selectedStations.postValue(listSelectedStations)
                _metroStations.postValue(listStations)
            }
    }

    fun searchStation(station: String) {
        if (station.isNullOrEmpty()) {
            _metroStations.postValue(listStations)
        } else {
            compositeDisposable += Maybe.fromCallable {
                listStations.filter { x -> x.name.contains(station, true) }
            }
                .performOnBackgroundOutOnMain()
                .subscribeSimple {
                    _metroStations.postValue(it)
                }
        }
    }

    fun selectStation(station: String) {
        if (listSelectedStations.contains(station)) {
            listSelectedStations.remove(station)
        } else listSelectedStations.add(station)
        _selectedStations.value = listSelectedStations
    }


    fun getSelectedStations() = listSelectedStations
}