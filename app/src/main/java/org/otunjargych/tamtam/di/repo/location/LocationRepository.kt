package org.otunjargych.tamtam.di.repo.location

import io.reactivex.Maybe
import org.otunjargych.tamtam.model.request.LocationRequestModel
import org.otunjargych.tamtam.model.request.LocationResponseModel
import org.otunjargych.tamtam.model.request.MetroStationsModel

interface LocationRepository {
    fun checkUserLocation(locationRequest: LocationRequestModel): Maybe<LocationResponseModel>
    fun findLocations(city: String): Maybe<List<LocationResponseModel>>
    fun getMetroStations(city: String?) : Maybe<List<MetroStationsModel>>
}