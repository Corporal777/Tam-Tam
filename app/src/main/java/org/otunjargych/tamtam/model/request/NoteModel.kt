package org.otunjargych.tamtam.model.request

import android.content.Context
import com.google.gson.annotations.SerializedName
import org.otunjargych.tamtam.R

data class NoteModel(
    val id: String,
    val name: String,
    val description: String?,
    val salary: String?,
    val category: Category,
    val status : Status,
    val createdBy: String,
    val createdAt: String,
    val images: List<String>?,
    val contacts: NoteContactsModel,
    val address: NoteAddressModel
){
    enum class Category {
        @SerializedName("work")
        WORK,

        @SerializedName("house")
        HOUSE
    }

    enum class Status {
        @SerializedName("approved")
        APPROVED,

        @SerializedName("pending")
        PENDING
    }

    fun getCategory(context : Context): String {
        return when (category) {
            Category.WORK -> context.getString(R.string.work_category)
            else -> context.getString(R.string.not_chosen)
        }
    }

    fun getLocation(): String {
        var location = address.city
        if (!address.metro.isNullOrEmpty()) {
            location = address.city + ", " + address.metro.first()
        }
        return location
    }
}


data class NoteContactsModel(
    val phone: List<String>?,
    val whatsapp: List<String>?,
    val email: List<String>?
)

data class NoteAddressModel(
    val region: String?,
    val city: String,
    val street: String?,
    val house: String?,
    val metro: List<String>?,
    val lat: String?,
    val lon: String?
)

data class NoteRequestBody(
    val name: String,
    val description: String?,
    val salary: String?,
    val category : String,
    val images: List<String>?,
    val contacts: NoteContactsModel,
    val address: NoteAddressModel,
)