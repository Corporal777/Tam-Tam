package org.otunjargych.tamtam.model.request

data class WorkNoteAdditionalData(
    val noteId : String,
    val subCategory : String,
    val workSchedule : String?,
    val workExperience : String?,
    val additionalInfo : String?
)