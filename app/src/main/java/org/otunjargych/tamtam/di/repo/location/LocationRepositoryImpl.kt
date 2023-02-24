package org.otunjargych.tamtam.di.repo.location

import io.reactivex.Maybe
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.user.UserRepository
import org.otunjargych.tamtam.model.request.LocationRequestModel
import org.otunjargych.tamtam.model.request.LocationResponseModel
import org.otunjargych.tamtam.model.request.MetroStationsModel
import org.otunjargych.tamtam.model.request.SpecialityModel

class LocationRepositoryImpl(val appData: AppData, val apiService: ApiService) :
    LocationRepository {


    override fun checkUserLocation(locationRequest: LocationRequestModel): Maybe<LocationResponseModel> {
        return apiService.checkUserLocation(locationRequest)
    }

    override fun findLocations(city: String): Maybe<List<LocationResponseModel>> {
        return apiService.findLocations(city)
    }

    override fun getMetroStations(city: String?): Maybe<List<MetroStationsModel>> {
        return apiService.getMetroStations(city?:"")
    }

    override fun findSpecialities(speciality: String): Maybe<List<SpecialityModel>> {
        return apiService.getSpecialities(speciality)
    }
}