package org.otunjargych.tamtam.util.rxtakephoto

import android.graphics.Bitmap
import io.reactivex.subjects.SingleSubject

object CropCallbackHelper {

    private val cropRequests = mutableMapOf<String, SingleSubject<Bitmap>>()
    private lateinit var multiplePickSubject: SingleSubject<List<Bitmap>>

    fun createRequest(key: String): SingleSubject<Bitmap> {
        return cropRequests.getOrPut(key) { SingleSubject.create() }
    }

    fun getRequest(key: String): SingleSubject<Bitmap>? {
        return cropRequests[key]
    }

    fun createMultiplePickSubject(): SingleSubject<List<Bitmap>> {
        multiplePickSubject = SingleSubject.create()
        return multiplePickSubject
    }

    fun getMultiplePickSubject() = multiplePickSubject
}