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
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.model.State


class DataViewModel : ViewModel() {

    private val _work: MutableLiveData<List<Node>> = MutableLiveData()
    val work: LiveData<List<Node>> = _work

    private val _health: MutableLiveData<List<Node>> = MutableLiveData()
    val health: LiveData<List<Node>> = _health

    private val _house: MutableLiveData<List<Node>> = MutableLiveData()
    val house: LiveData<List<Node>> = _house

    private val _transport: MutableLiveData<List<Node>> = MutableLiveData()
    val transport: LiveData<List<Node>> = _transport

    private val _services: MutableLiveData<List<Node>> = MutableLiveData()
    val services: LiveData<List<Node>> = _services

    private val _buySell: MutableLiveData<List<Node>> = MutableLiveData()
    val buySell: LiveData<List<Node>> = _buySell

    private val _data: MutableLiveData<State<List<Node>>> = MutableLiveData()
    val data: LiveData<State<List<Node>>> = _data




    fun loadMyNodes(userId: String) {
        val list = ArrayList<Node>()
        _data.postValue(State.Loading())
        viewModelScope.launch {
            getMyDataNodes(userId, NODE_WORKS).collect {
                list.addAll(it)
            }
        }
        viewModelScope.launch {
            getMyDataNodes(userId, NODE_HEALTH).collect {
                list.addAll(it)
            }
        }
        viewModelScope.launch {
            getMyDataNodes(userId, NODE_TRANSPORT).collect {
                list.addAll(it)
            }
        }
        viewModelScope.launch {
            getMyDataNodes(userId, NODE_SERVICES).collect {
                list.addAll(it)
            }
        }
        viewModelScope.launch {
            getMyDataNodes(userId, NODE_BUY_SELL).collect {
                list.addAll(it)
            }
        }
        viewModelScope.launch {
            getMyDataNodes(userId, NODE_HOUSE).collect {
                list.addAll(it)
                if (list.isNotEmpty()) {
                    _data.postValue(State.Success(list))
                } else
                    _data.postValue(State.NoItem())

            }
        }
    }


    private suspend fun getMyDataNodes(
        userId: String,
        collection: String
    ): Flow<List<Node>> =
        callbackFlow {
            val eventDocument = FirebaseFirestore
                .getInstance()
                .collection(collection)
                .whereEqualTo("userId", userId)

            val subscription = eventDocument.addSnapshotListener { snapshot, error ->
                if (!snapshot!!.isEmpty) {
                    val nodeList = snapshot.toObjects(Node::class.java)
                    offer(nodeList)
                }
            }

            awaitClose { subscription.remove() }
        }


}