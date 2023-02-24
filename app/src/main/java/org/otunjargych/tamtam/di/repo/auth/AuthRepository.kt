package org.otunjargych.tamtam.di.repo.auth

import com.google.common.base.Verify
import io.reactivex.Completable
import io.reactivex.Maybe
import org.otunjargych.tamtam.model.User
import org.otunjargych.tamtam.model.UserNew
import org.otunjargych.tamtam.model.request.*

interface AuthRepository {
    fun getToken(deviceId : String) : Maybe<TokenModel>
    fun login(body: LoginRequestBody): Maybe<UserNew>
    fun logout(id: String): Completable
    fun register(body: Map<String, String>): Maybe<UserNew>
    fun checkUserPassword(password: String): Completable
    fun updateUserPassword(password: Map<String, String>): Completable
    fun updateUserLogin(login: Map<String, String>): Completable
    fun checkIfLoginExists(login: String): Completable
    fun sendFcmToken(): Completable
    fun sendVerifyCode(body : RegisterLoginModel): Completable
    fun checkVerifyCode(body : VerifyLoginModel): Completable
}