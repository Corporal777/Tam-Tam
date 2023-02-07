package org.otunjargych.tamtam.api

import android.content.Context
import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.model.User
import org.otunjargych.tamtam.util.extensions.*
import org.otunjargych.tamtam.util.extensions.NODE_SERVICES
import org.otunjargych.tamtam.util.extensions.NODE_WORKS
import java.util.*
import kotlin.collections.ArrayList

object FireBaseHelper {

    private const val workCategory = "Работа, Вакансии"
    private const val transportCategory = "Транспорт, Перевозки"
    private const val healthCategory = "Медицина, Красота"
    private const val buySellCategory = "Продажа, Покупка"
    private const val flatsCategory = "Квартиры, Гостиницы"
    private const val studyCategory = "Обучение, Услуги"
    private val imagesUrlList = ArrayList<String>()
    private lateinit var mRefAds: DatabaseReference


    fun addNewData(category: String, note: Note) {
        mRefAds = FirebaseDatabase.getInstance().reference
        when (category) {
            healthCategory -> {
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
                    path.downloadUrl.addOnCompleteListener { status ->
                        if (status.isSuccessful) {
                            imagesUrlList.add( status.result.toString())
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

    fun changeViewingNumber(category: String, uuid: String, count: Int) {
        FF_DATABASE_ROOT.collection(NODE_VIP)
            .document(uuid)
            .update("viewings", count)

        when (category) {
            healthCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HEALTH)
                    .document(uuid)
                    .update("viewings", count)
            }
            workCategory -> {
                FF_DATABASE_ROOT.collection(NODE_WORKS)
                    .document(uuid)
                    .update("viewings", count)

            }
            studyCategory -> {
                FF_DATABASE_ROOT.collection(NODE_SERVICES)
                    .document(uuid)
                    .update("viewings", count)
            }
            flatsCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HOUSE)
                    .document(uuid)
                    .update("viewings", count)
            }
            transportCategory -> {
                FF_DATABASE_ROOT.collection(NODE_TRANSPORT)
                    .document(uuid)
                    .update("viewings", count)
            }
            buySellCategory -> {
                FF_DATABASE_ROOT.collection(NODE_BUY_SELL)
                    .document(uuid)
                    .update("viewings", count)
            }
        }
    }

    fun changeLikeNumber(category: String, uuid: String, count: Int) {

        FF_DATABASE_ROOT.collection(NODE_VIP)
            .document(uuid)
            .update("likes", count)

        when (category) {
            healthCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HEALTH)
                    .document(uuid)
                    .update("likes", count)
            }
            workCategory -> {
                FF_DATABASE_ROOT.collection(NODE_WORKS)
                    .document(uuid)
                    .update("likes", count)

            }
            studyCategory -> {
                FF_DATABASE_ROOT.collection(NODE_SERVICES)
                    .document(uuid)
                    .update("likes", count)
            }
            flatsCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HOUSE)
                    .document(uuid)
                    .update("likes", count)
            }
            transportCategory -> {
                FF_DATABASE_ROOT.collection(NODE_TRANSPORT)
                    .document(uuid)
                    .update("likes", count)
            }
            buySellCategory -> {
                FF_DATABASE_ROOT.collection(NODE_BUY_SELL)
                    .document(uuid)
                    .update("likes", count)
            }
        }
    }

    fun getImagesUrlList(): ArrayList<String> {
        return imagesUrlList
    }


    fun addDataToFirestore(category: String, note: Node) {
        when (category) {
            healthCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HEALTH)
                    .document(note.uuid)
                    .set(note)
            }
            workCategory -> {
                FF_DATABASE_ROOT.collection(NODE_WORKS)
                    .document(note.uuid)
                    .set(note)
            }
            studyCategory -> {
                FF_DATABASE_ROOT.collection(NODE_SERVICES)
                    .document(note.uuid)
                    .set(note)
            }
            flatsCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HOUSE)
                    .document(note.uuid)
                    .set(note)
            }
            transportCategory -> {
                FF_DATABASE_ROOT.collection(NODE_TRANSPORT)
                    .document(note.uuid)
                    .set(note)
            }
            buySellCategory -> {
                FF_DATABASE_ROOT.collection(NODE_BUY_SELL)
                    .document(note.uuid)
                    .set(note)
            }
        }
    }

    fun deleteNodeFromFirestore(context: Context, category: String, uuid: String) {
        when (category) {
            healthCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HEALTH).document(uuid)
                    .delete().addOnSuccessListener {
                        toastMessage(context, "Ваше объявление удалено")

                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            workCategory -> {
                FF_DATABASE_ROOT.collection(NODE_WORKS).document(uuid)
                    .delete().addOnSuccessListener {
                        toastMessage(context, "Ваше объявление удалено")

                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            studyCategory -> {
                FF_DATABASE_ROOT.collection(NODE_SERVICES).document(uuid)
                    .delete().addOnSuccessListener {
                        toastMessage(context, "Ваше объявление удалено")

                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            flatsCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HOUSE).document(uuid)
                    .delete().addOnSuccessListener {
                        toastMessage(context, "Ваше объявление удалено")

                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            transportCategory -> {
                FF_DATABASE_ROOT.collection(NODE_TRANSPORT).document(uuid)
                    .delete().addOnSuccessListener {
                        toastMessage(context, "Ваше объявление удалено")

                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            buySellCategory -> {
                FF_DATABASE_ROOT.collection(NODE_BUY_SELL).document(uuid)
                    .delete().addOnSuccessListener {
                        toastMessage(context, "Ваше объявление удалено")

                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
        }
    }

    fun editNodeFromFirestore(context: Context, category: String, node: Node) {
        when (category) {
            healthCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HEALTH)
                    .document(node.uuid)
                    .set(node).addOnSuccessListener {
                        toastMessage(context, "Ваше объявление изменено")
                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            workCategory -> {
                FF_DATABASE_ROOT.collection(NODE_WORKS)
                    .document(node.uuid)
                    .set(node).addOnSuccessListener {
                        toastMessage(context, "Ваше объявление изменено")
                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            studyCategory -> {
                FF_DATABASE_ROOT.collection(NODE_SERVICES)
                    .document(node.uuid)
                    .set(node).addOnSuccessListener {
                        toastMessage(context, "Ваше объявление изменено")
                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            flatsCategory -> {
                FF_DATABASE_ROOT.collection(NODE_HOUSE)
                    .document(node.uuid)
                    .set(node).addOnSuccessListener {
                        toastMessage(context, "Ваше объявление изменено")
                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            transportCategory -> {
                FF_DATABASE_ROOT.collection(NODE_TRANSPORT)
                    .document(node.uuid)
                    .set(node).addOnSuccessListener {
                        toastMessage(context, "Ваше объявление изменено")
                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
            buySellCategory -> {
                FF_DATABASE_ROOT.collection(NODE_BUY_SELL)
                    .document(node.uuid)
                    .set(node).addOnSuccessListener {
                        toastMessage(context, "Ваше объявление изменено")
                    }.addOnFailureListener {
                        toastMessage(context, "Что то пошло не так")
                    }
            }
        }
    }

    fun addNewUserProfile(user: User, uuid: String) {
        FF_DATABASE_ROOT.collection(NODE_USERS)
            .document(uuid)
            .set(user)
    }
}