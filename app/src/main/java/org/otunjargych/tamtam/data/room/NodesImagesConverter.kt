package org.otunjargych.tamtam.data.room

import androidx.room.TypeConverter

class NodesImagesConverter {

    @TypeConverter
    fun toListOfStrings(images: String): List<String> {
        return images.split(",")
    }
    @TypeConverter
    fun fromListOfStrings(listOfString: List<String>): String {
        return listOfString.joinToString(",")
    }
}