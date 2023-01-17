package org.otunjargych.tamtam.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.android.parcel.Parcelize
import org.otunjargych.tamtam.data.room.NodesImagesConverter

@Entity
@Parcelize
data class Node(
    var userId: String = "",
    @PrimaryKey
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
    @TypeConverters(NodesImagesConverter::class)
    var images: List<String> = ArrayList(),
    val phone_number: String = "",
    val whatsapp_number: String = ""
) : Parcelable