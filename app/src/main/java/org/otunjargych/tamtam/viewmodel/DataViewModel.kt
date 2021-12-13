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
import org.otunjargych.tamtam.api.FireBaseHelper.valueEventFlow
import org.otunjargych.tamtam.extensions.NODE_WORKS
import org.otunjargych.tamtam.model.Work

class DataViewModel : ViewModel() {


    private lateinit var mRefAds: DatabaseReference
    private val _data: MutableLiveData<List<Work>> = MutableLiveData()
    val data: LiveData<List<Work>> = _data

    fun loadWorkDataForMain(count : Int) {
        val workList: MutableList<Work> = ArrayList()
        viewModelScope.launch {
            mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_WORKS)
            mRefAds.valueEventFlow().collect { result ->
                when (result) {
                    is EventResponse.Changed -> {
                        val snapshot = result.snapshot
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            var work: Work = dataSnapshot.getValue(Work::class.java)!!
                            if (!workList.contains(work)) {
                                workList.add(0, work)
                                _data.value = workList
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