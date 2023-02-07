package org.otunjargych.tamtam.di.repo.note

import io.reactivex.Maybe
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.location.LocationRepository
import org.otunjargych.tamtam.model.request.NoteModel

class NotesRepositoryImpl(val appData: AppData, val apiService: ApiService) :
    NotesRepository {


    override fun getNotesList(): Maybe<List<NoteModel>> {
        return apiService.getNotes()
    }
}