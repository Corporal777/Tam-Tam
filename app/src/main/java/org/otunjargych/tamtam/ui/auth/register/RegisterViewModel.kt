package org.otunjargych.tamtam.ui.auth.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.auth.AuthRepository
import org.otunjargych.tamtam.di.repo.user.UserRepository
import org.otunjargych.tamtam.model.ContactInformation
import org.otunjargych.tamtam.model.UserNew
import org.otunjargych.tamtam.model.request.ErrorResponse
import org.otunjargych.tamtam.model.request.RegisterRequestBody
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.ui.views.PasswordData
import org.otunjargych.tamtam.util.AuthValidateUtil
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withLoadingDialog
import org.otunjargych.tamtam.util.isPhone
import org.otunjargych.tamtam.util.isPhoneIsValid
import retrofit2.HttpException
import kotlin.math.log

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appData: AppData
) : BaseViewModel() {

    private val _enableBtnRegister = MutableLiveData(false)
    val enableBtnRegister: LiveData<Boolean> get() = _enableBtnRegister

    private val _firstNameError = MutableLiveData(false)
    val firstNameError: LiveData<Boolean> get() = _firstNameError

    private val _lastNameError = MutableLiveData(false)
    val lastNameError: LiveData<Boolean> get() = _lastNameError

    private val _emailError = MutableLiveData(false)
    val emailError: LiveData<Boolean> get() = _emailError

    private val _passwordError = MutableLiveData(false)
    val passwordError: LiveData<Boolean> get() = _passwordError


    private val _successResponse = MutableLiveData<UserNew>()
    val successResponse: LiveData<UserNew> get() = _successResponse

    private val _errorResponse = MutableLiveData<String>()
    val errorResponse: LiveData<String> get() = _errorResponse

    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var password = PasswordData(false, "")
    private var loginType = ""


    fun register() {
        if (isDataValid()) {
            compositeDisposable += authRepository.register(getDataToSave())
                .doOnSuccess { appData.logIn(it.token, it.id.toInt()) }
                .flatMap { userRepository.getUserById(appData.getUserId()) }
                .doOnSuccess { appData.initUser(it) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(progressData)
                .subscribeBy(
                    onError = {
                        catchRegisterErrors(it)
                    },
                    onSuccess = { _successResponse.postValue(it) }
                )
        } else showErrors()
    }

    fun performNameChanged(value: String) {
        this.firstName = value
        _firstNameError.postValue(false)
        _enableBtnRegister.postValue(isDataValid())
    }

    fun performLastNameChanged(value: String) {
        this.lastName = value
        _lastNameError.postValue(false)
        _enableBtnRegister.postValue(isDataValid())
    }

    fun performEmailChanged(value: String) {
        this.email = value
        _emailError.postValue(false)
        _enableBtnRegister.postValue(isDataValid())
    }

    fun performPasswordChanged(value: PasswordData) {
        this.password = value
        _passwordError.postValue(false)
        _enableBtnRegister.postValue(isDataValid())
    }

    private fun isDataValid(): Boolean {
        val firstNameValid = !firstName.isNullOrBlank()
        val lastNameValid = !lastName.isNullOrBlank()
        val emailValid = if (isPhone(email)) isPhoneIsValid(email)
        else AuthValidateUtil.isValidEmail(email)

        val passwordValid =
            if (password.isValid) {
                AuthValidateUtil.isValidPassword(password.password ?: "")
            } else false
        return firstNameValid && lastNameValid && passwordValid && emailValid
    }

    private fun showErrors() {
        _firstNameError.postValue(firstName.isNullOrBlank())
        _lastNameError.postValue(lastName.isNullOrBlank())

        if (isPhone(email)) _emailError.postValue(!isPhoneIsValid(email))
        else _emailError.postValue(!AuthValidateUtil.isValidEmail(email))

        _passwordError.postValue(password.isValid)
    }


    private fun getDataToSave(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            put("firstName", firstName)
            put("lastName", lastName)
            put("login", email)
            put("password", password.password ?: "")
        }
    }


    private fun catchRegisterErrors(it: Throwable) {
        it.printStackTrace()
        if (it is HttpException) {
            try {
                val error = Gson().fromJson(
                    it.response()?.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                when (it.code()) {
                    400, 409 -> _errorResponse.postValue(error.message)
                }
            } catch (e: Exception) {

            }
        }
    }

    fun setFieldsWithGoogleAccountData(name: String, familyName: String, email: String) {
        this.firstName = name
        this.lastName = familyName
        this.email = email
    }

    enum class RegisterDataErrorType {
        NULL, PHONE_ERROR, EMAIL_ERROR, PASSWORD,
    }
}