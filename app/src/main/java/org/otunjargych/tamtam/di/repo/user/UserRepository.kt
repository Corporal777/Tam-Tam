package org.otunjargych.tamtam.di.repo.user

import android.graphics.Bitmap
import io.reactivex.Maybe
import io.reactivex.Single
import org.otunjargych.tamtam.model.ImageModel
import org.otunjargych.tamtam.model.User
import org.otunjargych.tamtam.model.UserNew

interface UserRepository {
    fun getCurrentUser() : Maybe<UserNew>
    fun getUserById(id : String) : Maybe<UserNew>
    fun getUsersList() : Maybe<List<User>>
    fun changeUserImage(photo: Bitmap?): Single<ImageModel>
    fun updateUser(data: Map<String, String?>): Maybe<UserNew>
}