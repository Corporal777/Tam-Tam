package org.otunjargych.tamtam.model.request

import android.content.Context
import androidx.core.text.isDigitsOnly
import com.google.common.base.CharMatcher
import com.google.gson.annotations.SerializedName
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.util.isContainLetters
import org.otunjargych.tamtam.util.isPhone

data class NoteModel(
    val id: String,
    val name: String,
    val description: String?,
    val salary: String?,
    val category: Category,
    val status: Status,
    val createdBy: String,
    val createdAt: String,
    val images: List<String>?,
    val contacts: NoteContactsModel,
    val address: NoteAddressModel,
    val likes: List<String>?,
    var isNoteLiked: Boolean = false,
    var isOwner: Boolean = false
) {
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

    fun getCategory(): String {
        return when (category) {
            Category.WORK -> "Работа, Вакансии"
            Category.HOUSE -> "Квартиры, Гостиницы"
            else -> "Не указано"
        }
    }

    fun getLocation(): String {
        var location = address.city
        if (!address.metro.isNullOrEmpty()) {
            location = address.city + ", " + address.metro.first()
        }
        return location
    }

    fun getNoteSalary(): String {
        var formattedSalary = ""
        if (salary.isNullOrBlank()) formattedSalary = "Не указано"
        else if (salary.contentEquals("договорная", true)) formattedSalary = salary
        else if (salary.contentEquals("Не указано", true)) formattedSalary = salary
        else if (isContainLetters(salary) && !isPhone(salary)) formattedSalary = "Не указано"
        else if (salary.contains("руб")) {
            formattedSalary = salary.replace("руб", "₽")
        } else {
            formattedSalary = CharMatcher.inRange('0', '9').retainFrom(salary) + " ₽"
        }
        return formattedSalary
    }

    fun getNoteDescription(): String {
        return if (description.isNullOrEmpty()) name.replace("\n", " ")
        else description.replace("\n", " ")
    }

    fun setNoteLiked(userId: String){
        isNoteLiked = likes?.contains(userId) == true
    }

    fun setOwner(userId: String){
        isOwner = userId == createdBy
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
    val category: String,
    val images: List<String>?,
    val contacts: NoteContactsModel,
    val address: NoteAddressModel,
)

data class NoteLikeBody(
    val noteId: String,
    val userId: String
)
