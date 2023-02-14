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
import org.otunjargych.tamtam.model.request.NoteAddressModel
import org.otunjargych.tamtam.model.request.NoteContactsModel
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.model.request.NoteRequestBody
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

    private val listSelectedImages = arrayListOf<Bitmap>()

    private val _selectedImages = MutableLiveData<List<Bitmap>>()
    val selectedImages: LiveData<List<Bitmap>> get() = _selectedImages

    private val _noteCreated = MutableLiveData<Boolean>()
    val noteCreated: LiveData<Boolean> get() = _noteCreated

    fun createNote(
        data: Map<String, String>,
        noteAddress: NoteAddressModel,
        contacts: NoteContactsModel,
        noteData: MutableMap<String, String>
    ) {
        val noteRequestBody = NoteRequestBody(
            data["name"]!!,
            data["description"],
            noteData["salary"],
            data["category"]!!,
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