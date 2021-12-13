package org.otunjargych.tamtam.api


import org.otunjargych.tamtam.model.Stations
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("/2.0/suggest/town/4/metro/all/")
    suspend fun getMetroStations(): Response<Stations>
}