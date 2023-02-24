package org.otunjargych.tamtam.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.auth.AuthRepository
import org.otunjargych.tamtam.di.repo.user.UserRepository
import org.otunjargych.tamtam.model.UserNew
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.extensions.call
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withDelay
import org.otunjargych.tamtam.util.extensions.withLoadingDialog
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    val appData: AppData
) : BaseViewModel() {

    private val _userTown = MutableLiveData<String>()
    val userTown: LiveData<String> get() = _userTown

    private val _tokenUpdated = MutableLiveData<Boolean>(false)
    val tokenUpdated: LiveData<Boolean> get() = _tokenUpdated


    init {
        loadToken()
    }

    private fun loadToken() {
        compositeDisposable += authRepository.getToken(appData.getDeviceUID())
            .timeout(2000, TimeUnit.MILLISECONDS)
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    observeTown()
                    _tokenUpdated.postValue(true)
                    observeUserChange()
                },
                onSuccess = {
                    observeTown()
                    _tokenUpdated.postValue(true)
                    observeUserChange()
                })
    }

    private fun observeTown() {
        compositeDisposable += Maybe.just(appData.getUserTown())
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                _userTown.postValue(it)
            }
    }

    private fun observeUserChange() {
        compositeDisposable += appData.userIdChangeSubject
            .subscribeSimple {
                authRepository.sendFcmToken()
                    .subscribeSimple { Log.e("FCM TOKEN: ", "was sent successfully!") }
                    .call(compositeDisposable)
            }
    }


    fun isUserAuthorized() = appData.isUserAuthorized()

}