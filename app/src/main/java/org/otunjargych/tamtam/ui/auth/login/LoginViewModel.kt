package org.otunjargych.tamtam.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.auth.AuthRepository
import org.otunjargych.tamtam.di.repo.user.UserRepository
import org.otunjargych.tamtam.model.UserNew
import org.otunjargych.tamtam.model.request.LoginRequestBody
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.AuthValidateUtil
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withDelay
import org.otunjargych.tamtam.util.extensions.withLoadingDialog
import org.otunjargych.tamtam.util.isPhone
import org.otunjargych.tamtam.util.isPhoneIsValid

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appData: AppData
) : BaseViewModel() {

    private val _enableBtnLogin = MutableLiveData(false)
    val enableBtnLogin: LiveData<Boolean> get() = _enableBtnLogin

    private val _loginError = MutableLiveData(false)
    val loginError: LiveData<Boolean> get() = _loginError

    private val _passwordError = MutableLiveData(false)
    val passwordError: LiveData<Boolean> get() = _passwordError

    private val _errorResponse = MutableLiveData(false)
    val errorResponse: LiveData<Boolean> get() = _errorResponse

    private val _successResponse = MutableLiveData<UserNew>()
    val successResponse: LiveData<UserNew> get() = _successResponse

    private var login = ""
    private var password = ""
    var fromNote = false
    var fromProfile = false

    fun login() {
        if (isDataValid()) {
            compositeDisposable += authRepository.login(LoginRequestBody(login, password))
                .withDelay(1000)
                .doOnSuccess { appData.logIn(it.token, it.id.toInt()) }
                .flatMap { userRepository.getUserById(appData.getUserId()) }
                .doOnSuccess { appData.initUser(it) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(progressData)
                .subscribeBy(
                    onError = {
                        it.printStackTrace()
                        _errorResponse.postValue(true)
                    }, onSuccess = {
                        _successResponse.postValue(it)
                    }
                )
        }
    }

    fun performChangeLogin(login: String) {
        this.login = login
        _loginError.postValue(false)
        _enableBtnLogin.postValue(!this.login.isNullOrEmpty())
    }

    fun performChangePassword(password: String) {
        this.password = password
        _passwordError.postValue(false)
        _enableBtnLogin.postValue(!this.password.isNullOrEmpty())
    }

    private fun isDataValid(): Boolean {
        val loginValid = if (isPhone(login)) isPhoneIsValid(login)
        else AuthValidateUtil.isValidEmail(login)
        val passwordValid = AuthValidateUtil.isValidPassword(password)
        _passwordError.postValue(!passwordValid)
        _loginError.postValue(!loginValid)
        return loginValid && passwordValid
    }
}