package org.otunjargych.tamtam.di.repo.note

import io.reactivex.Maybe
import org.otunjargych.tamtam.model.UserNotesModel
import org.otunjargych.tamtam.model.request.NoteModel

interface NotesRepository {
    fun getNotesList() : Maybe<List<NoteModel>>
}