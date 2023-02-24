package org.otunjargych.tamtam.ui.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.note.NotesRepository
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withDelay

class MyNotesViewModel(
    private val appData: AppData,
    private val notesRepository: NotesRepository
) : BaseViewModel() {

    private var currentPosition = 0

    private val _tabPosition = MutableLiveData<Int>()
    val tabPosition: LiveData<Int> get() = _tabPosition

    private val _activeNotes = MutableLiveData<List<NoteModel?>>()
    val activeNotes: LiveData<List<NoteModel?>> get() = _activeNotes

    private val _inActiveNotes = MutableLiveData<List<NoteModel?>>()
    val inActiveNotes: LiveData<List<NoteModel?>> get() = _inActiveNotes

    fun setTabPosition(position: Int) {
        currentPosition = position
        _tabPosition.value = position
    }


    fun getActiveNotes() {
        _activeNotes.postValue(List(6) { null })
        compositeDisposable += notesRepository.getNotesListByParams(mutableMapOf<String, String>().apply {
            put(FILTER_USER, appData.getUserId())
            put(FILTER_STATUS, "approved")
        })
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    _activeNotes.postValue(emptyList())
                    it.printStackTrace()
                },
                onSuccess = {
                    _activeNotes.postValue(it)
                })
    }

    fun getInactiveNotes(){
        _inActiveNotes.postValue(List(6) { null })
        compositeDisposable += notesRepository.getNotesListByParams(mutableMapOf<String, String>().apply {
            put(FILTER_USER, appData.getUserId())
            put(FILTER_STATUS, "pending")
        })
            .withDelay(1000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    _inActiveNotes.postValue(emptyList())
                    it.printStackTrace()
                },
                onSuccess = {
                    _inActiveNotes.postValue(it)
                })
    }

    companion object {
        const val FILTER_USER = "userId"
        const val FILTER_STATUS = "status"
    }
}