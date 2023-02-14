package org.otunjargych.tamtam.di.repo.note

import android.graphics.Bitmap
import com.google.gson.JsonElement
import io.reactivex.Completable
import io.reactivex.Maybe
import okhttp3.RequestBody
import org.otunjargych.tamtam.model.UserNotesModel
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.model.request.NoteRequestBody

interface NotesRepository {
    fun getNotesList(): Maybe<List<NoteModel>>
    fun createNote(body: NoteRequestBody): Maybe<NoteModel>
    fun createNoteData(noteId: String, map : Map<String, String?>): Maybe<String>
    fun uploadNoteImages(noteId: String, body: RequestBody) : Maybe<NoteModel>
}