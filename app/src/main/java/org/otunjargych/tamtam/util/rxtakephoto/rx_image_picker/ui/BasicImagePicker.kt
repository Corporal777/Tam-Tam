package org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.ui

import android.content.Context
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.entity.sources.Camera
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.entity.sources.Gallery
import io.reactivex.Observable
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.entity.Result

interface BasicImagePicker {

    @Gallery
    fun openGallery(context: Context): Observable<Result>

    @Camera
    fun openCamera(context: Context): Observable<Result>
}