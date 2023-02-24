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
import org.otunjargych.tamtam.model.request.*
import org.otunjargych.tamtam.util.toBodyPart

class NotesRepositoryImpl(val appData: AppData, val apiService: ApiService) :
    NotesRepository {


    override fun getNotesList(): Maybe<List<NoteModel>> {
        return apiService.getNotes().doOnSuccess { it.forEach { note -> setNoteFields(note) } }
    }

    override fun getNotesListByParams(map: Map<String, String>): Maybe<List<NoteModel>> {
        return apiService.getNotesByParams(map).doOnSuccess {
            it.forEach { note -> setNoteFields(note) }
        }
    }

    override fun createNote(body: NoteRequestBody): Maybe<NoteModel> {
        return apiService.createNote(appData.getUserId(), body).doOnSuccess { setNoteFields(it) }
    }

    override fun uploadNoteImages(noteId: String, body: RequestBody): Maybe<NoteModel> {
        return apiService.changeNoteImages(noteId, body).doOnSuccess { setNoteFields(it) }
    }

    override fun createNoteData(noteId: String, map: Map<String, String?>): Maybe<String> {
        return apiService.createNoteData(noteId, map).map {
            val obj = it.asJsonObject
            obj["noteId"].asString
        }
    }

    override fun createNoteDraft(draft: NoteDraftModel): Maybe<NoteDraftResponseModel> {
        return apiService.createNoteDraft(appData.getUserId(), draft)
    }

    override fun loadNoteDraft(): Maybe<NoteDraftResponseModel> {
        return apiService.getNoteDraft(appData.getUserId())
    }

    override fun addNoteToFavorite(noteId: String): Maybe<NoteModel> {
        return if (appData.isUserAuthorized()) {
            apiService.addNoteToFavorite(NoteLikeBody(noteId, appData.getUserId()))
                .doOnSuccess { setNoteFields(it) }
        } else Maybe.error(NullPointerException("User is not authorized!"))
    }

    override fun removeNoteFromFavorite(noteId: String): Maybe<NoteModel> {
        return if (appData.isUserAuthorized()) {
            apiService.removeNoteFromFavorite(NoteLikeBody(noteId, appData.getUserId()))
                .doOnSuccess { setNoteFields(it) }
        } else Maybe.error(NullPointerException("User is not authorized!"))

    }

    override fun getNoteDetail(id: String): Maybe<NoteDetailModel> {
        return apiService.getNoteDetail(id).doOnSuccess { setNoteFields(it.note) }
    }

    private fun setNoteFields(note: NoteModel) {
        note.setNoteLiked(appData.getUserId())
        note.setOwner(appData.getUserId())
    }
}