package org.otunjargych.tamtam.ui.town

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.otunjargych.tamtam.di.repo.location.LocationRepository
import org.otunjargych.tamtam.model.request.LocationResponseModel
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain

class CitiesViewModel(private val locationRepository: LocationRepository) : BaseViewModel() {

    private val _cities = MutableLiveData<List<LocationResponseModel>>()
    val cities: LiveData<List<LocationResponseModel>> get() = _cities

    fun findCities(city: String) {
        compositeDisposable += locationRepository.findLocations(city)
            .performOnBackgroundOutOnMain()
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = { _cities.postValue(it) }
            )
    }


}