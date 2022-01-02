package org.otunjargych.tamtam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.api.EventResponse
import org.otunjargych.tamtam.api.FireBaseHelper.valuesEventFlow
import org.otunjargych.tamtam.extensions.NODE_USERS
import org.otunjargych.tamtam.model.State

class UserViewModel : ViewModel() {
    private lateinit var mRefAds: DatabaseReference
    private val _user: MutableLiveData<State<DataSnapshot>> = MutableLiveData()
    val user: LiveData<State<DataSnapshot>> = _user

    fun loadUserData(uuid : String) {
        _user.postValue(State.Loading())
        viewModelScope.launch {
            mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_USERS).child(uuid)
            mRefAds.valuesEventFlow().collect { result ->
                when (result) {
                    is EventResponse.Changed -> {
                        val snapshot = result.snapshot
                        _user.postValue(State.Success(snapshot))
                    }
                    is EventResponse.Cancelled -> {
                        _user.postValue(State.Error())
                        val exception = result.error
                    }
                }
            }
        }
    }

}