package org.otunjargych.tamtam.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.note.NotesRepository
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withDelay

class HomeViewModel(private val appData: AppData, private val notesRepository: NotesRepository) :
    BaseViewModel() {

    private val _notesList = MutableLiveData<List<NoteModel?>>()
    val notesList: LiveData<List<NoteModel?>> get() = _notesList


    init {
        getNotesList()
    }

    private fun getNotesList() {
        _notesList.postValue(List(6) { null })
        compositeDisposable += notesRepository.getNotesList()
            .withDelay(3000)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    _notesList.postValue(emptyList())
                    it.printStackTrace()
                },
                onSuccess = {
                    _notesList.postValue(it)
                })
    }
}