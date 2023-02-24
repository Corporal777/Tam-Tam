package org.otunjargych.tamtam.ui.createNote

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.note.NotesRepository
import org.otunjargych.tamtam.model.request.*
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withLoadingDialog
import org.otunjargych.tamtam.util.rxtakephoto.RxTakePhoto
import java.io.ByteArrayOutputStream

class CreateNoteViewModel(
    private val appData: AppData,
    private val rxTakePhoto: RxTakePhoto,
    private val notesRepository: NotesRepository
) : BaseViewModel() {

    private var isFirstLaunch = true
    private val listSelectedImages = arrayListOf<Bitmap>()

    private val _selectedImages = MutableLiveData<List<Bitmap>>()
    val selectedImages: LiveData<List<Bitmap>> get() = _selectedImages

    private val _noteCreated = MutableLiveData<Boolean>()
    val noteCreated: LiveData<Boolean> get() = _noteCreated

    private val _draftCreated = MutableLiveData<Boolean>()
    val draftCreated: LiveData<Boolean> get() = _draftCreated

    private val _noteDraft = MutableLiveData<NoteDraftModel?>()
    val noteDraft: LiveData<NoteDraftModel?> get() = _noteDraft

    init {
        loadNoteDraft()
    }

    private fun loadNoteDraft() {
        compositeDisposable += notesRepository.loadNoteDraft()
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    _noteDraft.postValue(null)
                },
                onSuccess = {
                    _noteDraft.postValue(it.draft)
                })
    }

    fun createNote(
        data: Map<String, String>,
        noteAddress: NoteAddressModel,
        contacts: NoteContactsModel,
        noteData: MutableMap<String, String>
    ) {
        val noteRequestBody = NoteRequestBody(
            data["name"]?:"",
            data["description"],
            noteData["salary"]?:"",
            data["category"]?:"",
            null,
            contacts,
            noteAddress
        )
        compositeDisposable += notesRepository.createNote(noteRequestBody)
            .flatMap { notesRepository.createNoteData(it.id, noteData) }
            .flatMapCompletable { id -> getImageRequest(id) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple {
                _noteCreated.postValue(true)
            }
    }

    fun showGallery() {
        compositeDisposable += rxTakePhoto.takeMultipleImages()
            .doOnSuccess {
                listSelectedImages.clear()
                listSelectedImages.addAll(it)
            }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _selectedImages.postValue(it)
            }
    }

    fun removeSelectedImage(bitmap: Bitmap) {
        compositeDisposable += Completable.fromAction {
            listSelectedImages.remove(bitmap)
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _selectedImages.postValue(listSelectedImages)
            }
    }

    fun createNoteDraft(draft: NoteDraftModel) {
        compositeDisposable += notesRepository.createNoteDraft(draft)
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                },
                onSuccess = {
                    _draftCreated.postValue(true)
                })
    }

    private fun getImageRequest(noteId: String): Completable {
        if (listSelectedImages.isNullOrEmpty()) {
            return Completable.complete()
        } else {
            val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .apply {
                    listSelectedImages.forEachIndexed { index, bitmap ->
                        val byteArray = ByteArrayOutputStream().let {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                            it.toByteArray()
                        }

                        val body =
                            byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                        addFormDataPart(
                            "file$index",
                            "image$index.png",
                            body
                        )
                    }
                }
                .build()
            return notesRepository.uploadNoteImages(noteId, body).ignoreElement()
        }
    }

    fun getCurrentTown() = appData.getUserTown()
}