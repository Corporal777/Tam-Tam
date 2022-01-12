package org.otunjargych.tamtam.api

import android.net.Uri
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.model.User
import java.util.*
import kotlin.collections.ArrayList

object FireBaseHelper {

    private const val workCategory = "Работа, Вакансии"
    private const val transportCategory = "Транспорт, Перевозки"
    private const val medicineCategory = "Медицина, Красота"
    private const val buySellCategory = "Продажа, Покупка"
    private const val flatsCategory = "Квартиры, Гостиницы"
    private const val studyCategory = "Обучение, Услуги"
    private val imagesUrlList = ArrayList<String>()
    private lateinit var mRefAds: DatabaseReference


    suspend fun DatabaseReference.valuesEventFlow(): Flow<EventResponse> = callbackFlow {
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


    suspend fun DatabaseReference.lastValuesEventFlow(count: Int): Flow<EventResponse> =
        callbackFlow {
            val valueEventListener = object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot): Unit =
                    sendBlocking(EventResponse.Changed(snapshot))

                override fun onCancelled(error: DatabaseError): Unit =
                    sendBlocking(EventResponse.Cancelled(error))
            }
            limitToLast(count).addListenerForSingleValueEvent(valueEventListener)
            awaitClose {
                removeEventListener(valueEventListener)
            }
        }

    fun addNewData(category: String, note: Note) {
        mRefAds = FirebaseDatabase.getInstance().reference
        when (category) {
            medicineCategory -> {
                mRefAds.child(NODE_HEALTH).child(Date().time.toString())
                    .setValue(note)
            }
            workCategory -> {
                mRefAds.child(NODE_WORKS).child(Date().time.toString())
                    .setValue(note)
            }
            studyCategory -> {
                mRefAds.child(NODE_SERVICES).child(Date().time.toString())
                    .setValue(note)
            }
            flatsCategory -> {
                mRefAds.child(NODE_HOUSE).child(Date().time.toString())
                    .setValue(note)
            }
            transportCategory -> {
                mRefAds.child(NODE_TRANSPORT).child(Date().time.toString())
                    .setValue(note)
            }
            buySellCategory -> {
                mRefAds.child(NODE_BUY_SELL).child(Date().time.toString())
                    .setValue(note)
            }
        }
    }

    fun addImagesToStorage(list: List<Uri>) {
        imagesUrlList.clear()

        list.forEach {
            val path =
                FirebaseStorage.getInstance().reference.child(
                    FOLDER_NOTES_IMAGES
                ).child(UUID.randomUUID().toString())
            path.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    path.downloadUrl.addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            imagesUrlList.add(result.result.toString())
                        }
                    }
                }
            }
        }
    }


    fun changeUserPhoto(uuid: String, url: Uri) {
        val path =
            FirebaseStorage.getInstance().reference.child(FOLDER_USER_IMAGES).child(USER_ID)
        path.putFile(url).addOnCompleteListener {
            if (it.isSuccessful) {
                path.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FF_DATABASE_ROOT.collection(NODE_USERS)
                            .document(uuid)
                            .update("image", task.result.toString())
                    }
                }
            }
        }
    }

    fun changeUserName(uuid: String, changedName: String, changedLastName: String) {
        FF_DATABASE_ROOT.collection(NODE_USERS)
            .document(uuid)
            .update("name", changedName, "last_name", changedLastName)
    }

    fun getImagesUrlList(): ArrayList<String> {
        return imagesUrlList

    }


    fun addDataToFirestore(category: String, note: Node) {
        when (category) {
            medicineCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HEALTH)
                    .add(note)
            }
            workCategory -> {
                FF_DATABASE_ROOT.collection(NODE_WORKS)
                    .add(note)
            }
            studyCategory -> {
                FF_DATABASE_ROOT.collection(NODE_SERVICES)
                    .add(note)
            }
            flatsCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HOUSE)
                    .add(note)
            }
            transportCategory -> {
                FF_DATABASE_ROOT.collection(NODE_TRANSPORT)
                    .add(note)
            }
            buySellCategory -> {
                FF_DATABASE_ROOT.collection(NODE_BUY_SELL)
                    .add(note)
            }
        }
    }


    fun addNewUserProfile(user: User, uuid: String) {
        FF_DATABASE_ROOT.collection(NODE_USERS)
            .document(uuid)
            .set(user)
            .addOnSuccessListener { documentReference ->
                //Log.d("Success", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Error", "Error adding document", e)
            }
    }
}