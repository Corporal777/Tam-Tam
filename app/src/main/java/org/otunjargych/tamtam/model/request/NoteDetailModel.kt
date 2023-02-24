package org.otunjargych.tamtam.model.request

import com.google.gson.JsonElement

data class NoteDetailModel(
    val note: NoteModel,
    val additionalData: JsonElement?
)