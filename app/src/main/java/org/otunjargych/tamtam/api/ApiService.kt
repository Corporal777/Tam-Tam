package org.otunjargych.tamtam.api


import org.otunjargych.tamtam.extensions.END_POINT
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.model.Stations
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/2.0/suggest/town/4/metro/all/")
    suspend fun getMetroStations(): Response<Stations>

    @GET(END_POINT)
    suspend fun loadWorkNotes(
        @Query("orderBy") orderBy: String,
        @Query("limitToLast") limitToLast: Int
    ): Response<Note>
}