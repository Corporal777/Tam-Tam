package org.otunjargych.tamtam.model.request


data class WorkNoteAdditionalData(
    val noteId: String,
    val subCategory: String,
    val workSchedule: String?,
    val workExperience: String?,
    val workSpeciality: String?,
    val workComment: String?
)

data class NoteDraftResponseModel(
    val user: String,
    val draft: NoteDraftModel
)

data class NoteDraftModel(
    val name: String,
    val description: String?,
    val salary: String?,
    val category : String,
    val contacts: NoteContactsModel,
    val address: NoteAddressModel,
    val additionalData: String
)

data class WorkNoteDataDraft(
    val subCategory: String,
    val workSchedule: String?,
    val workExperience: String?,
    val workSpeciality: String?,
    val workComment: String?
)

data class HouseNoteDataDraft(
    val subCategory: String,
    val houseType: String?,
    val houseRooms: String?,
    val houseTerm: String?,
    val houseComment: String?
)