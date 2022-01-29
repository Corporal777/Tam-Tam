package org.otunjargych.tamtam.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.data.AllNodesDataSource
import org.otunjargych.tamtam.data.SearchNodesDataSource
import org.otunjargych.tamtam.extensions.FF_DATABASE_ROOT
import org.otunjargych.tamtam.extensions.NODE_VIP
import org.otunjargych.tamtam.extensions.PAGE_SIZE
import org.otunjargych.tamtam.extensions.TIME_PROPERTY
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.model.State


class NodeViewModel() : ViewModel() {

    private val _node: MutableLiveData<State<List<Node>>> = MutableLiveData()
    val node: LiveData<State<List<Node>>> = _node

    private val _vip: MutableLiveData<List<Node>> = MutableLiveData()
    val vip: LiveData<List<Node>> = _vip


    fun loadActualNodes(collection: String) {
        _node.value = State.Loading()
        viewModelScope.launch {
            delay(1000)
            getActualData(collection).collect {
                if (it.isNotEmpty()) {
                    _node.postValue(State.Success(it))
                } else {
                    Log.e("Empty", "onSuccess: LIST EMPTY");
                }
            }
        }
    }


    fun loadVipNodes() {
        viewModelScope.launch {
            getVipFirestoreData().collect {
                if (it.isNotEmpty()) {
                    _vip.postValue(it)
                } else {
                    Log.e("Empty", "onSuccess: LIST EMPTY");
                }
            }
        }
    }


    fun loadNodes(collection: String): Flow<PagingData<Node>> {
        val queryNodes = FF_DATABASE_ROOT
            .collection(collection)
            .orderBy(TIME_PROPERTY, DESCENDING)
            .limit(PAGE_SIZE.toLong())

        val listData =
            Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                AllNodesDataSource(queryNodes)
            }.flow.cachedIn(viewModelScope)

        return listData
    }

    fun loadSearchNodes(collection: String, search: String): Flow<PagingData<Node>> {
        val queryNodes = FF_DATABASE_ROOT
            .collection(collection)
            .orderBy(TIME_PROPERTY, DESCENDING)
            .limit(PAGE_SIZE.toLong())

        val searchData =
            Pager(PagingConfig(pageSize = PAGE_SIZE)) {
                SearchNodesDataSource(search, queryNodes)
            }.flow.cachedIn(viewModelScope)

        return searchData
    }


    private suspend fun getActualData(collection: String): Flow<List<Node>> =
        callbackFlow {
            val eventDocument = FirebaseFirestore
                .getInstance()
                .collection(collection)
                .orderBy(TIME_PROPERTY, DESCENDING)
                .limit(6)

            val subscription = eventDocument.addSnapshotListener { snapshot, error ->
                if (!snapshot!!.isEmpty) {
                    val nodeList = snapshot.toObjects(Node::class.java)
                    offer(nodeList)
                }
            }

            awaitClose { subscription.remove() }
        }



    private fun getVipFirestoreData(): Flow<List<Node>> =
        callbackFlow {
            val eventDocument = FirebaseFirestore
                .getInstance()
                .collection(NODE_VIP)

            val subscription = eventDocument.addSnapshotListener { snapshot, error ->
                if (!snapshot!!.isEmpty) {
                    val nodeList = snapshot.toObjects(Node::class.java)
                    offer(nodeList)
                }
            }

            awaitClose { subscription.remove() }
        }
}