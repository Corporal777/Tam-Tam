package org.otunjargych.tamtam.api


import com.google.gson.JsonElement
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.otunjargych.tamtam.model.*
import org.otunjargych.tamtam.model.request.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // auth routes
    @GET("v1/get-token/{deviceId}")
    fun getToken(@Path("deviceId") deviceId: String) : Maybe<TokenModel>

    @POST("v1/logout/{id}")
    fun logout(@Path("id") id: String): Completable

    @POST("v1/login")
    fun login(@Body body: LoginRequestBody): Maybe<UserNew>

    @POST("v1/register")
    fun register(@Body body: Map<String, String>): Maybe<UserNew>

    @GET("v1/check-password/{id}")
    fun checkUserPassword(
        @Path("id") id: String,
        @Query("password") password: String
    ): Completable

    @PATCH("v1/change-password/{id}")
    fun updateUserPassword(
        @Path("id") id: String,
        @Body map: Map<String, String>
    ): Completable

    @GET("v1/check-login/{login}")
    fun checkLoginExists(@Path("login") login: String): Completable

    @PATCH("v1/change-login/{id}")
    fun updateUserLogin(
        @Path("id") id: String,
        @Body map: Map<String, String>
    ): Completable

    @POST("v1/save-fcm-token")
    fun sendFcmToken(@Body body: Map<String, String>): Completable

    @POST("v1/send-verify-code")
    fun sendVerifyCode(@Body body: RegisterLoginModel): Completable

    @POST("v1/check-verify-code")
    fun checkVerifyCode(@Body body: VerifyLoginModel): Completable


    // user routes
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


    //location routes
    @POST("/v1/check-user-location")
    fun checkUserLocation(@Body body: LocationRequestModel): Maybe<LocationResponseModel>

    @GET("/v1/find-cities")
    fun findLocations(@Query("city") city: String): Maybe<List<LocationResponseModel>>

    @GET("v1/metro-stations")
    fun getMetroStations(@Query("city") city: String): Maybe<List<MetroStationsModel>>

    //specialities route
    @GET("v1/specialities-list")
    fun getSpecialities(@Query("speciality") speciality: String): Maybe<List<SpecialityModel>>


    //note routes
    @GET("v1/notes")
    fun getNotes(): Maybe<List<NoteModel>>

    @GET("v1/notes-list")
    fun getNotesByParams(@QueryMap map: Map<String, String>): Maybe<List<NoteModel>>

    @POST("v1/create-note/{id}")
    fun createNote(
        @Path("id") id: String,
        @Body body: NoteRequestBody
    ): Maybe<NoteModel>

    @POST("v1/create-note-data/{id}")
    fun createNoteData(
        @Path("id") id: String,
        @Body map: Map<String, String?>
    ): Maybe<JsonElement>

    @POST("v1/change-note-images/{id}")
    fun changeNoteImages(
        @Path("id") id: String,
        @Body image: RequestBody
    ): Maybe<NoteModel>

    @POST("v1/create-note-draft/{id}")
    fun createNoteDraft(
        @Path("id") id: String,
        @Body body: NoteDraftModel
    ): Maybe<NoteDraftResponseModel>

    @GET("v1/get-note-draft/{id}")
    fun getNoteDraft(@Path("id") id: String): Maybe<NoteDraftResponseModel>

    @PATCH("v1/add-note-favorite")
    fun addNoteToFavorite(@Body body: NoteLikeBody) : Maybe<NoteModel>

    @PATCH("v1/delete-note-favorite")
    fun removeNoteFromFavorite(@Body body: NoteLikeBody) : Maybe<NoteModel>

    @GET("v1/note-detail/{id}")
    fun getNoteDetail(@Path("id") id: String): Maybe<NoteDetailModel>

    // story routes
    @GET("v1/stories")
    fun getStoriesList(): Maybe<List<StoriesModel>>
}