package org.otunjargych.tamtam.di.repo.note

import android.graphics.Bitmap
import com.google.gson.JsonElement
import io.reactivex.Completable
import io.reactivex.Maybe
import okhttp3.RequestBody
import org.otunjargych.tamtam.model.UserNotesModel
import org.otunjargych.tamtam.model.request.*

interface NotesRepository {
    fun getNotesList(): Maybe<List<NoteModel>>
    fun getNotesListByParams(map: Map<String, String>): Maybe<List<NoteModel>>
    fun createNote(body: NoteRequestBody): Maybe<NoteModel>
    fun createNoteData(noteId: String, map : Map<String, String?>): Maybe<String>
    fun uploadNoteImages(noteId: String, body: RequestBody) : Maybe<NoteModel>

    fun createNoteDraft(draft : NoteDraftModel) : Maybe<NoteDraftResponseModel>
    fun loadNoteDraft() : Maybe<NoteDraftResponseModel>

    fun addNoteToFavorite(noteId: String) : Maybe<NoteModel>
    fun removeNoteFromFavorite(noteId: String) : Maybe<NoteModel>

    fun getNoteDetail(id : String): Maybe<NoteDetailModel>
}