package org.otunjargych.tamtam.di.repo.user

import android.graphics.Bitmap
import io.reactivex.Maybe
import io.reactivex.Single
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.model.ImageModel
import org.otunjargych.tamtam.model.User
import org.otunjargych.tamtam.model.UserNew
import org.otunjargych.tamtam.util.toBodyPart

class UserRepositoryImpl(val appData: AppData, val apiService: ApiService) : UserRepository {

    override fun getCurrentUser(): Maybe<UserNew> {
        return if (appData.userId == null || appData.userId == -1)
            Maybe.error(NullPointerException("User is not authorized!"))
        else apiService.getUserById(appData.getUserId())
            .doOnSuccess { appData.updateUser(it) }
    }

    override fun getUserById(id: String): Maybe<UserNew> = apiService.getUserById(id)

    override fun getUsersList(): Maybe<List<User>> {
        return apiService.getUsersList()
    }

    override fun changeUserImage(photo: Bitmap?): Single<ImageModel> {
        return apiService.changeUserImage(
            appData.getUserId(),
            photo?.toBodyPart("file", "image.png")
        )
    }

    override fun updateUser(data: Map<String, String?>): Maybe<UserNew> {
        return apiService.updateUser(appData.getUserId(), data)
    }
}