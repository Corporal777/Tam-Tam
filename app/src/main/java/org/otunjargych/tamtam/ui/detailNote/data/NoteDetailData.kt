package org.otunjargych.tamtam.ui.detailNote.data

import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.model.request.WorkNoteAdditionalData

sealed class NoteDetailData<T>(val field: NoteModel, var value: T?) {


    abstract fun getData(): T

    class WorkData(field: NoteModel, value: WorkNoteAdditionalData?) : NoteDetailData<WorkNoteAdditionalData>(field, value) {
        override fun getData(): WorkNoteAdditionalData = value!!

    }


}