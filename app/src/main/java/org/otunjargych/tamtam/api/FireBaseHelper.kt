package org.otunjargych.tamtam.api

import android.net.Uri
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.model.Note
import java.util.*
import kotlin.collections.ArrayList

object FireBaseHelper {

    private const val workCategory = "Работа, Подработки"
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

    suspend fun FirebaseFirestore.getFirestoreData(collection: String): Flow<EventResponse> = callbackFlow {
        val document = FirebaseFirestore
            .getInstance().collection(collection)
        val listener = document.addSnapshotListener { snapshot, _ ->
            sendBlocking(EventResponse.Loaded(snapshot))
        }
        awaitClose {
            listener.remove()
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


    fun changeUserPhoto(url: Uri, snapShot: DataSnapshot) {
        val path =
            FirebaseStorage.getInstance().reference.child(FOLDER_USER_IMAGES).child(USER_ID)
        path.putFile(url).addOnCompleteListener {
            if (it.isSuccessful) {
                path.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (snapShot != null) {
                            val image: DatabaseReference = snapShot.ref.child("image")
                            image.setValue(task.result.toString())
                        }
                    }
                }
            }
        }
    }

    fun changeUserName(changedName: String, changedLastName: String, snapShot: DataSnapshot) {
        if (snapShot != null) {
            val name: DatabaseReference = snapShot.ref.child("name")
            val lastName: DatabaseReference = snapShot.ref.child("last_name")
            name.setValue(changedName)
            lastName.setValue(changedLastName)
        }
    }

    fun getImagesUrlList(): ArrayList<String> {
        return imagesUrlList

    }


    fun addToFireStore(note: Note) {
        FF_DATABASE_ROOT.collection("work_notes")
            .add(note)
            .addOnSuccessListener { documentReference ->
                Log.d("Success", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Error", "Error adding document", e)
            }
    }
}