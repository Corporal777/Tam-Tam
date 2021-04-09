package myapp.firstapp.tamtam.model

import com.google.gson.annotations.SerializedName

class Stations(
    @SerializedName("objects")
    val stations: ArrayList<Station>
)

class Station(
    @SerializedName("title")
    val title: String
)
