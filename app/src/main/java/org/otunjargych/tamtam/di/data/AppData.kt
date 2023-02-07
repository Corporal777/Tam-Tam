package org.otunjargych.tamtam.di.data

import android.util.Log
import io.reactivex.subjects.BehaviorSubject
import org.otunjargych.tamtam.model.ContactInformation
import org.otunjargych.tamtam.model.UserNew
import org.otunjargych.tamtam.model.UserNotesModel

class AppData(private val appPrefs: AppPrefs) {

    var token: String? = appPrefs.userToken
        set(value) {
            val changed = field != value
            if (changed) {
                if (value.isNullOrEmpty()) {
                    field = null
                    appPrefs.userToken = null
                    tokenChangeSubject.onNext(Optional())
                } else {
                    field = value
                    appPrefs.userToken = value
                    tokenChangeSubject.onNext(value.asOptional())
                }
            }
        }

    var userId: Int? = appPrefs.userId
        set(value) {
            val changed = field != value
            if (changed) {
                if (value == null || value == -1) {
                    field = -1
                    appPrefs.userId = -1
                } else {
                    field = value
                    appPrefs.userId = value
                }
            }
        }

    private var userTown: String? = appPrefs.userTown
        set(value) {
            val changed = field != value
            if (changed) {
                if (value.isNullOrEmpty()) {
                    field = null
                    appPrefs.userTown = null
                } else {
                    field = value
                    appPrefs.userTown = value
                }
            }
        }


    private var user: UserNew? = null
    private var isLoggedOut =
        token.isNullOrEmpty() && (userId == null || userId == -1) && user == null

    val tokenChangeSubject = BehaviorSubject.createDefault(token.asOptional())
    val userChangeSubject = BehaviorSubject.createDefault(user.asOptional())


    fun logIn(token: String, id: Int) {
        this.userId = id
        this.token = token
        this.isLoggedOut = false
    }

    fun logOut() {
        this.token = null
        this.userId = -1
        this.isLoggedOut = true
        this.user = null
    }

    fun initUser(user: UserNew) {
        val changed = this.user != user
        if (changed){
            this.user = user
            userChangeSubject.onNext(user.asOptional())
        }
    }

    fun getUser(): UserNew?
    = this.user
//        this.user ?: UserNew(
//            "76002",
//            "Mads",
//            "Mikkelsen",
//            "",
//            null,
//            ContactInformation("+79267806176", "podxvat@gmail.com"),
//            UserNotesModel(null, null)
//        )

    fun updateUserImage(image: String) {
        this.user?.image = image
    }

    fun updateUser(newUser: UserNew) {
        val changed = this.user != newUser
        this.user = newUser
        if (changed){
            userChangeSubject.onNext(user.asOptional())
        }
    }

    fun getUserId(): String = this.userId.toString()
    fun isLoggedOut() = this.isLoggedOut


    fun getUserTown() = this.userTown
    fun initUserTown(town: String) {
        this.userTown = town
    }
}