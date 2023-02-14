package org.otunjargych.tamtam.model.request

data class NoteModel(
    val id: String,
    val name: String,
    val description: String?,
    val salary: String?,
    val category: String,
    val createdBy: String,
    val createdAt: String,
    val images: List<String>?,
    val contacts: NoteContactsModel,
    val address: NoteAddressModel
){
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