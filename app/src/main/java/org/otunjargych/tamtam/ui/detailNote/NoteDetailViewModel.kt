package org.otunjargych.tamtam.ui.detailNote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.note.NotesRepository
import org.otunjargych.tamtam.model.request.NoteDetailModel
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.model.request.WorkNoteAdditionalData
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.ui.detailNote.data.NoteDetailData
import org.otunjargych.tamtam.util.extensions.performOnBackground
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withLoadingDialog
import org.otunjargych.tamtam.util.fromJson
import org.otunjargych.tamtam.util.toSingleEvent

class NoteDetailViewModel(
    private val appData: AppData,
    private val notesRepository: NotesRepository
) : BaseViewModel() {


    private var scrollOffset = 0
    private val _scrollOffsetData = MutableLiveData<Int>(scrollOffset)
    val scrollOffsetData get() = _scrollOffsetData.toSingleEvent()

    private val _noteDetail = MutableLiveData<NoteDetailData<*>>()
    val noteDetail: LiveData<NoteDetailData<*>> get() = _noteDetail


    fun loadNoteDetail(noteId: String) {
        compositeDisposable += notesRepository.getNoteDetail(noteId)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(loadingData)
            .map { createNoteDetailData(it) }
            .subscribeSimple {
                if (it != null) _noteDetail.postValue(it)
            }
    }

    private fun createNoteDetailData(noteDetail: NoteDetailModel): NoteDetailData<*>? {
        return when (noteDetail.note.category) {
            NoteModel.Category.WORK -> {
                NoteDetailData.WorkData(
                    noteDetail.note,
                    noteDetail.additionalData.fromJson<WorkNoteAdditionalData>()
                )
            }
            else -> null
        }
    }

    fun changeScrollOffset(value: Int) {
        scrollOffset = value
        _scrollOffsetData.postValue(scrollOffset)
    }


}