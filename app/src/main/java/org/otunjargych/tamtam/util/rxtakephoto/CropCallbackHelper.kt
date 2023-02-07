package org.otunjargych.tamtam.util.rxtakephoto

import android.graphics.Bitmap
import io.reactivex.subjects.SingleSubject

object CropCallbackHelper {

    private val cropRequests = mutableMapOf<String, SingleSubject<Bitmap>>()

    fun createRequest(key: String): SingleSubject<Bitmap> {
        return cropRequests.getOrPut(key) { SingleSubject.create() }
    }

    fun getRequest(key: String): SingleSubject<Bitmap>? {
        return cropRequests[key]
    }
}