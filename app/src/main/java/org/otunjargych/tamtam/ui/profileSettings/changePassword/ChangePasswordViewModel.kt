package org.otunjargych.tamtam.ui.profileSettings.changePassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.rxkotlin.plusAssign
import org.otunjargych.tamtam.di.repo.auth.AuthRepository
import org.otunjargych.tamtam.model.request.ErrorResponse
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.ui.views.PasswordData
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withDelay
import org.otunjargych.tamtam.util.extensions.withLoadingDialog
import retrofit2.HttpException

class ChangePasswordViewModel(private val authRepository: AuthRepository) : BaseViewModel() {

    private var currentPassword = ""
    private var newPassword = ""
    private var newPasswordValid = false

    private val _currentPasswordCorrect = MutableLiveData<Boolean>(false)
    val currentPasswordCorrect: LiveData<Boolean> get() = _currentPasswordCorrect

    private val _passwordError = MutableLiveData<Boolean>()
    val passwordError: LiveData<Boolean> get() = _passwordError

    private val _passwordSuccessUpdated = MutableLiveData<Boolean>()
    val passwordSuccessUpdated: LiveData<Boolean> get() = _passwordSuccessUpdated


    fun checkCurrentPasswordIsCorrect() {
        if (currentPassword.isNullOrBlank()) {
            _passwordError.value = true
        } else {
            compositeDisposable += authRepository.checkUserPassword(currentPassword)
                .withDelay(1000)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(progressData)
                .subscribeSimple(
                    onError = {
                        if (it is HttpException) {
                            val error = Gson().fromJson(
                                it.response()?.errorBody()?.string(),
                                ErrorResponse::class.java
                            )
                            if (error.message == "User password is not correct!") {
                                _passwordError.postValue(true)
                            }
                        }
                    },
                    onComplete = {
                        _passwordError.postValue(false)
                        _currentPasswordCorrect.postValue(true)
                    }
                )
        }

    }

    fun updateUserPassword() {
        if (newPasswordValid){
            compositeDisposable += authRepository.updateUserPassword(getNewPassword())
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(progressData)
                .subscribeSimple {
                    _passwordSuccessUpdated.postValue(true)
                }
        }else _passwordError.value = true
    }


    fun performChangeCurrentPassword(password: String) {
        this.currentPassword = password
        _passwordError.value = false
    }

    fun performChangeNewPassword(password: PasswordData) {
        this.newPassword = password.password ?: ""
        this.newPasswordValid = password.isValid
        _passwordError.value = false
    }

    private fun getNewPassword() = mapOf("password" to newPassword)
}