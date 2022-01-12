package org.otunjargych.tamtam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.extensions.NODE_USERS
import org.otunjargych.tamtam.model.User

class UserViewModel : ViewModel() {

    private val _user: MutableLiveData<User> = MutableLiveData()
    var user: LiveData<User> = _user

    private val _currentUser: MutableLiveData<User> = MutableLiveData()
    var currentUser: LiveData<User> = _currentUser

    fun loadUserData(uuid: String) {
        viewModelScope.launch {
            getUser(uuid).collect {user->
                if (user != null) {
                    _user.postValue(user)
                }
            }
        }
    }

    fun loadCurrentUserData(uuid: String) {
        viewModelScope.launch {
            getUser(uuid).collect {
                if (it != null) {
                    _currentUser.value = it
                }
            }
        }
    }


    private suspend fun getUser(uuid: String): Flow<User> =
        callbackFlow {
            val eventDocument = FirebaseFirestore
                .getInstance()
                .collection(NODE_USERS)
                .document(uuid)

            val subscription = eventDocument.addSnapshotListener { value, error ->
                if (value?.exists()!! && value != null) {
                    val user = value.toObject(User::class.java)
                    if (user != null) {
                        offer(user)
                    }
                }
            }
            awaitClose { subscription }
        }


}