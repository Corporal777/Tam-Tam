package org.otunjargych.tamtam.model.request

import com.google.gson.annotations.SerializedName
import org.otunjargych.tamtam.model.ContactInformation

data class RegisterRequestBody(
    @SerializedName("firstName")
    val firstName : String,
    @SerializedName("lastName")
    val lastName : String,
    val login : String,
    val password : String,
    val image : String = "",
    val contactInformation: ContactInformation
)
