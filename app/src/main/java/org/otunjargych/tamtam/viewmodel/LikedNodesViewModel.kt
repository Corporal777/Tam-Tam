package org.otunjargych.tamtam.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.data.room.DatabaseBuilder
import org.otunjargych.tamtam.data.room.DatabaseHelperImpl
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.model.State

class LikedNodesViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(context))
    private val _liked: MutableLiveData<State<List<Node>>> = MutableLiveData()
    val liked: LiveData<State<List<Node>>> = _liked

    private var _check: MutableLiveData<List<Node>> = MutableLiveData()
    val check: LiveData<List<Node>> = _check

    init {
        fetchLikedNodes()
    }

    private fun fetchLikedNodes() {
        _liked.postValue(State.Loading())
        viewModelScope.launch {
            delay(1000)
            try {
                val likedNodes = dbHelper.getNodes()
                _liked.postValue(State.Success(likedNodes))
            } catch (e: Exception) {
                _liked.postValue(State.Error())
                Log.e("Room Error get", e.message.toString())
            }
        }
    }

    fun insertNodesData(node: Node) {
        viewModelScope.launch {
            try {
                dbHelper.insertNode(node)
                Log.e("Error", "Success")
            } catch (e: Exception) {
                Log.e("Room Error insert", e.message.toString())
            }
        }
    }

    fun checkLikedNode() {
        viewModelScope.launch {
            try {
                val likedNodes = dbHelper.getNodes()
                _check.postValue(likedNodes)
            } catch (e: Exception) {
                Log.e("Room Error get", e.message.toString())
            }
        }
    }

}