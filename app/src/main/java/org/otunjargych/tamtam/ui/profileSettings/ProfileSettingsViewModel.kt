package org.otunjargych.tamtam.ui.profileSettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.auth.AuthRepository
import org.otunjargych.tamtam.di.repo.user.UserRepository
import org.otunjargych.tamtam.model.ContactInformation
import org.otunjargych.tamtam.model.UserNew
import org.otunjargych.tamtam.model.UserNotesModel
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.extensions.IMAGE_MAX_SIZE_AVATAR
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withLoadingDialog
import org.otunjargych.tamtam.util.rxtakephoto.ResultRotation
import org.otunjargych.tamtam.util.rxtakephoto.RxTakePhoto

class ProfileSettingsViewModel(
    private val userRepository: UserRepository,
    private val rxTakePhoto: RxTakePhoto,
    private val appData: AppData
) : BaseViewModel() {

    private val _enableBtnSave = MutableLiveData(false)
    val enableBtnSave: LiveData<Boolean> get() = _enableBtnSave

    private val _user = MutableLiveData<UserNew?>()
    val user: LiveData<UserNew?> get() = _user

    private val _userUpdated = MutableLiveData<Boolean>()
    val userUpdated: LiveData<Boolean> get() = _userUpdated

    private var firstName = appData.getUser()?.firstName
    private var lastName = appData.getUser()?.lastName
    private var email = appData.getUser()?.contacts?.email
    private var phone = appData.getUser()?.contacts?.phone

    init {
        loadUser()
    }

    private fun loadUser() {
        compositeDisposable += Maybe.defer { Maybe.just(appData.getUser()) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    _user.postValue(null)
                },
                onSuccess = {
                    _user.postValue(it)
                })
    }

    fun takeGalleryImage() = takePhoto(rxTakePhoto.takeGalleryImage())
    fun takeCameraImage() = takePhoto(rxTakePhoto.takeCameraImage())

    private fun takePhoto(takePhotoRequest: Observable<ResultRotation>) {
        compositeDisposable += takePhotoRequest
            .firstOrError()
            .flatMap {
                rxTakePhoto.crop(
                    resultRotation = it,
                    outputMaxWidth = IMAGE_MAX_SIZE_AVATAR,
                    outputMaxHeight = IMAGE_MAX_SIZE_AVATAR,
                    cropMode = CropImageView.CropMode.SQUARE
                )
            }
            .flatMap { userRepository.changeUserImage(it) }
            .doOnSuccess { appData.updateUserField { image = it.image } }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(progressData)
            .subscribeBy(
                onError = { it.printStackTrace() },
                onSuccess = { _user.postValue(appData.getUser()) }
            )
    }

    fun updateUser() {
        if (isDataValid()) {
            compositeDisposable += userRepository.updateUser(getDataToSave())
                .doOnSuccess { appData.updateUser(it) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(progressData)
                .subscribeSimple(
                    onError = {
                        it.printStackTrace()
                    },
                    onSuccess = {
                        _user.postValue(it)
                        _userUpdated.postValue(true)
                    })
        }
    }


    fun performChangeFirstName(name: String) {
        this.firstName = name
        _enableBtnSave.postValue(isDataValid())
    }

    fun performChangeLastName(lastName: String) {
        this.lastName = lastName
        _enableBtnSave.postValue(isDataValid())
    }

    fun performChangeEmail(email: String) {
        this.email = email
        _enableBtnSave.postValue(isDataValid())
    }

    fun performChangePhone(phone: String) {
        this.phone = phone
        _enableBtnSave.postValue(isDataValid())
    }

    private fun isDataValid(): Boolean {
        val nameValid = !firstName.isNullOrBlank()
        val lastNameValid = !lastName.isNullOrBlank()
        val loginValid = !appData.getUser()?.login.isNullOrBlank()
        return nameValid && lastNameValid && loginValid
    }

    private fun getDataToSave(): MutableMap<String, String?> {
        return mutableMapOf<String, String?>().apply {
            put("firstName", firstName)
            put("lastName", lastName)
            put("email", email)
            put("phone", phone)
        }
    }
}