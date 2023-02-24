package org.otunjargych.tamtam.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.rxkotlin.plusAssign
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.auth.AuthRepository
import org.otunjargych.tamtam.di.repo.note.NotesRepository
import org.otunjargych.tamtam.di.repo.story.StoryRepository
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.model.request.StoriesModel
import org.otunjargych.tamtam.model.request.StoryModel
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.SingleLiveEvent
import org.otunjargych.tamtam.util.extensions.call
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withDelay
import org.otunjargych.tamtam.util.extensions.withLoadingDialog
import org.otunjargych.tamtam.util.toSingleEvent

class HomeViewModel(
    private val appData: AppData,
    private val notesRepository: NotesRepository,
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    private val _notesList = MutableLiveData<List<NoteModel?>>()
    val notesList = _notesList.toSingleEvent()

    private val _storiesList = MutableLiveData<List<StoriesModel>>()
    val storiesList: LiveData<List<StoriesModel>> get() = _storiesList

    private val _updatedNote = MutableLiveData<NoteModel>()
    val updatedNote = _updatedNote.toSingleEvent()


    init {
        getNotesList()
        getStoriesList()
    }

    private fun getNotesList() {
        _notesList.postValue(List(6) { null })
        compositeDisposable += notesRepository.getNotesList()
            .withDelay(1000)
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

    private fun getStoriesList() {
        compositeDisposable += storyRepository.getStoriesList()
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _storiesList.postValue(it)
            }
    }


    fun addNoteToFavorite(noteId: String) {
        compositeDisposable += notesRepository.addNoteToFavorite(noteId)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _updatedNote.postValue(it)
            }
    }

    fun removeNoteFromFavorite(noteId: String) {
        compositeDisposable += notesRepository.removeNoteFromFavorite(noteId)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _updatedNote.postValue(it)
            }
    }


}