package org.otunjargych.tamtam.api

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.otunjargych.tamtam.model.Note
import java.util.*

object FireBaseHelper {

    private lateinit var mRefAds: DatabaseReference
    suspend fun DatabaseReference.valueEventFlow(): Flow<EventResponse> = callbackFlow {

        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot): Unit =
                sendBlocking(EventResponse.Changed(snapshot))

            override fun onCancelled(error: DatabaseError): Unit =
                sendBlocking(EventResponse.Cancelled(error))
        }
        addListenerForSingleValueEvent(valueEventListener)
        awaitClose {
            removeEventListener(valueEventListener)
        }
    }

    fun addNewData(str : String, data : Note){
        mRefAds = FirebaseDatabase.getInstance().reference
        mRefAds.child(str).child(Date().time.toString())
            .setValue(data)
    }
}