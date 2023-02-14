package org.otunjargych.tamtam.di.repo.note

import android.graphics.Bitmap
import android.net.Uri
import com.google.gson.JsonElement
import io.reactivex.Completable
import io.reactivex.Maybe
import okhttp3.RequestBody
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.location.LocationRepository
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.model.request.NoteRequestBody
import org.otunjargych.tamtam.util.toBodyPart

class NotesRepositoryImpl(val appData: AppData, val apiService: ApiService) :
    NotesRepository {


    override fun getNotesList(): Maybe<List<NoteModel>> {
        return apiService.getNotes()
    }

    override fun createNote(body: NoteRequestBody): Maybe<NoteModel> {
        return apiService.createNote(appData.getUserId(), body)
    }

    override fun uploadNoteImages(noteId: String, body: RequestBody): Maybe<NoteModel> {
        return apiService.changeNoteImages(noteId, body)
    }

    override fun createNoteData(noteId: String, map: Map<String, String?>): Maybe<String> {
        return apiService.createNoteData(noteId, map).map {
            val obj = it.asJsonObject
            obj["noteId"].asString
        }
    }
}