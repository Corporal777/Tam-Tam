package org.otunjargych.tamtam.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Node(
    var userId: String = "",
    var uuid: String = "",
    var title: String = "",
    var text: String = "",
    var salary: String = "",
    var timeStamp: Long = 0,
    var invertedTimeStamp: String = "",
    var station: String = "",
    var addres: String = "",
    var category: String = "",
    var likes: Int = 0,
    var viewings: Int = 0,
    var images: List<String> = ArrayList(),
    val phone_number: String = "",
    val whatsapp_number: String = ""
): Parcelable