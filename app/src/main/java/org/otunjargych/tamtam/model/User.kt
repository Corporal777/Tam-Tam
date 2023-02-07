package org.otunjargych.tamtam.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String = "",
    var name: String = "",
    var last_name: String = "",
    val phone_number: String = "",
    var email: String = "",
    var image: String = ""
)


data class UserNew(
    val id: String,
    val firstName: String,
    val lastName: String,
    val login: String,
    var image: String?,
    @SerializedName("contactsInformation")
    val contacts: ContactInformation,
    val userNotes: UserNotesModel
) {
    val nameLastName: String
        get() = "$firstName $lastName"
    val publicId: String
        get() = "id$id"
    val contactEmail: String
        get() = contacts.email ?: ""
    val contactPhone: String
        get() = contacts.phone ?: ""
}

data class ContactInformation(
    val phone: String?,
    val email: String?
)

data class UserNotesModel(
    var activeNotes: List<String>?,
    var moderationNotes: List<String>?
)