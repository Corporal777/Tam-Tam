package org.otunjargych.tamtam.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.api.EventResponse
import org.otunjargych.tamtam.api.FireBaseHelper.lastValuesEventFlow
import org.otunjargych.tamtam.api.FireBaseHelper.valuesEventFlow
import org.otunjargych.tamtam.extensions.NODE_WORKS
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.model.State

class DataViewModel : ViewModel() {


    private lateinit var mRefAds: DatabaseReference
    private val _data: MutableLiveData<State<List<Note>>> = MutableLiveData()
    val data: LiveData<State<List<Note>>> = _data

    private val _last: MutableLiveData<List<Note>> = MutableLiveData()
    val last: LiveData<List<Note>> = _last

    fun loadNoteData(folder: String) {
        val list: MutableList<Note> = ArrayList()
        _data.postValue(State.Loading())
        viewModelScope.launch {
            delay(1000)
            mRefAds = FirebaseDatabase.getInstance().reference.child(folder)
            mRefAds.valuesEventFlow().collect { result ->
                when (result) {
                    is EventResponse.Changed -> {
                        val snapshot = result.snapshot
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            var note = dataSnapshot.getValue(Note::class.java)!!
                            if (!list.contains(note)) {
                                list.add(0, note)
                                _data.postValue(State.Success(list))
                            }

                        }

                    }
                    is EventResponse.Cancelled -> {
                        _data.postValue(State.Error())
                        val exception = result.error
                    }
                }
            }
        }
    }

    fun loadLastNoteData(count: Int) {
        val list: MutableList<Note> = ArrayList()
        viewModelScope.launch {
            mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_WORKS)
            mRefAds.lastValuesEventFlow(count).collect { result ->
                when (result) {
                    is EventResponse.Changed -> {
                        val snapshot = result.snapshot
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            var note = dataSnapshot.getValue(Note::class.java)!!
                            if (!list.contains(note)) {
                                list.add(0, note)
                                _last.value = list
                            }

                        }

                    }
                    is EventResponse.Cancelled -> {
                        val exception = result.error
                    }
                }
            }
        }
    }


}