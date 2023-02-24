package org.otunjargych.tamtam.di.repo.auth

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Maybe
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.model.UserNew
import org.otunjargych.tamtam.model.request.*

class AuthRepositoryImpl(val appData: AppData, val apiService: ApiService) : AuthRepository {

    override fun getToken(deviceId: String): Maybe<TokenModel> {
        return apiService.getToken(deviceId).doOnSuccess { appData.saveToken(it.token) }
    }

    override fun login(body: LoginRequestBody): Maybe<UserNew> {
        return apiService.login(body).doOnSuccess { appData.login(it) }
    }

    override fun logout(id: String): Completable {
        return apiService.logout(id)
    }

    override fun register(body: Map<String, String>): Maybe<UserNew> {
        return apiService.register(body).doOnSuccess { appData.login(it) }
    }

    override fun checkUserPassword(password: String): Completable {
        return apiService.checkUserPassword(appData.getUserId(), password)
    }

    override fun updateUserPassword(password: Map<String, String>): Completable {
        return apiService.updateUserPassword(appData.getUserId(), password)
    }

    override fun updateUserLogin(login: Map<String, String>): Completable {
        return apiService.updateUserLogin(appData.getUserId(), login)
    }

    override fun checkIfLoginExists(login: String): Completable {
        return apiService.checkLoginExists(login)
    }

    override fun sendFcmToken(): Completable {
        return FireBaseHelper.getFcmToken()
            .flatMapCompletable { token ->
                apiService.sendFcmToken(
                    mapOf(
                        "deviceId" to appData.getDeviceUID(),
                        "userId" to appData.getUserId(),
                        "token" to token
                    )
                )
            }
    }

    override fun sendVerifyCode(body: RegisterLoginModel): Completable {
        return apiService.sendVerifyCode(body)
    }

    override fun checkVerifyCode(body: VerifyLoginModel): Completable {
        return apiService.checkVerifyCode(body)
    }
}



