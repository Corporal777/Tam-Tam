package org.otunjargych.tamtam.di.repo.auth

import io.reactivex.Completable
import io.reactivex.Maybe
import org.otunjargych.tamtam.model.User
import org.otunjargych.tamtam.model.request.LoginRequestBody
import org.otunjargych.tamtam.model.request.LoginResponse
import org.otunjargych.tamtam.model.request.RegisterRequestBody

interface AuthRepository {
    fun login(body : LoginRequestBody) : Maybe<LoginResponse>
    fun logout(id : String) : Completable
    fun register(body: Map<String, String>) : Maybe<LoginResponse>
}