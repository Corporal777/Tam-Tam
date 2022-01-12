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
import org.otunjargych.tamtam.data.NotesDataSource
import org.otunjargych.tamtam.extensions.FF_DATABASE_ROOT
import org.otunjargych.tamtam.extensions.PAGE_SIZE
import org.otunjargych.tamtam.extensions.TIME_PROPERTY
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.model.State


class NodeViewModel() : ViewModel() {

    private val _node: MutableLiveData<State<List<Node>>> = MutableLiveData()
    val node: LiveData<State<List<Node>>> = _node

    fun loadSearchableNodes(collection: String) {
        _node.value = State.Loading()
        viewModelScope.launch {
            delay(1500)
            getFirestoreData(collection).collect {
                if (it.data!!.isNotEmpty()) {
                    _node.postValue(State.Success(it.data))
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
                NotesDataSource(queryNodes)
            }.flow.cachedIn(viewModelScope)

        return listData
    }


    private suspend fun getFirestoreData(collection: String): Flow<State<List<Node>>> =
        callbackFlow {
            val eventDocument = FirebaseFirestore
                .getInstance()
                .collection(collection)

            val subscription = eventDocument.addSnapshotListener { snapshot, error ->
                if (!snapshot!!.isEmpty) {
                    val nodeList = snapshot.toObjects(Node::class.java)
                    offer(State.Success(nodeList))
                }
            }

            awaitClose { subscription.remove() }
        }
}