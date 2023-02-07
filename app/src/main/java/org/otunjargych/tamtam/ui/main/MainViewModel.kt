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
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.user.UserRepository
import org.otunjargych.tamtam.model.UserNew
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.extensions.performOnBackgroundOutOnMain
import org.otunjargych.tamtam.util.extensions.withLoadingDialog

class MainViewModel(
    private val userRepository: UserRepository,
    val appData: AppData
) : BaseViewModel() {

    private val _userTown = MutableLiveData<String?>(appData.getUserTown())
    val userTown: LiveData<String?> get() = _userTown

    init {
        observeTokenChange()
    }

    private fun observeTokenChange() {
        compositeDisposable += appData.tokenChangeSubject
            .subscribeBy(
                onError = { it.printStackTrace() },
                onNext = { opt -> }
            )
    }


}