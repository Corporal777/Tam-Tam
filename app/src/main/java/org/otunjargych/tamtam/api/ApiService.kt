package org.otunjargych.tamtam.api


import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import org.otunjargych.tamtam.model.*
import org.otunjargych.tamtam.model.request.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @GET("v1/notes")
    fun getNotes(): Maybe<List<NoteModel>>

    @POST("v1/logout/{id}")
    fun logout(@Path("id") id: String): Completable

    @POST("v1/login")
    fun login(@Body body: LoginRequestBody): Maybe<LoginResponse>

    @POST("v1/register")
    fun register(@Body body: Map<String, String>): Maybe<LoginResponse>

    @GET("/users")
    fun getUsersList(): Maybe<List<User>>

    @GET("v1/user/{id}")
    fun getUserById(@Path("id") id: String): Maybe<UserNew>

    @Multipart
    @POST("v1/change-user-photo/{id}")
    fun changeUserImage(
        @Path("id") id: String,
        @Part image: MultipartBody.Part?
    ): Single<ImageModel>

    @POST("/v1/update-user/{id}")
    fun updateUser(
        @Path("id") id: String,
        @Body map: Map<String, String?>
    ): Maybe<UserNew>


    @POST("/v1/check-user-location")
    fun checkUserLocation(@Body body: LocationRequestModel): Maybe<LocationResponseModel>

    @GET("/v1/find-cities")
    fun findLocations(@Query("city") city: String): Maybe<List<LocationResponseModel>>

    @GET("v1/metro-stations")
    fun getMetroStations(@Query("city") city: String): Maybe<List<MetroStationsModel>>
}