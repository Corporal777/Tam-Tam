package org.otunjargych.tamtam.util.rxtakephoto

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.FragmentActivity
import org.otunjargych.tamtam.util.rxtakephoto.CropActivity.Companion.CROP_MODE_DEFAULT
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.core.RxImagePicker
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.entity.Result
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.otunjargych.tamtam.ui.main.MainActivity
import java.io.IOException


class RxTakePhoto(private val context: MainActivity) {

    //private val rxPermissions = RxPermissions(context)
    private val rxImagePicker = RxImagePicker.create()

    fun takeCameraImage(): Observable<ResultRotation> {
//        return rxPermissions.request(
//            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        ).flatMap {
//            if (it) rxImagePicker.openCamera(context).findRotation()
//            else Observable.error<ResultRotation>(PermissionNotGrantedException())
//        }
        return rxImagePicker.openCamera(context).findRotation()
    }

    fun takeGalleryImage(): Observable<ResultRotation> {
//        return rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
//            .flatMap {
//                if (it) rxImagePicker.openGallery(context).findRotation()
//                else Observable.error<ResultRotation>(PermissionNotGrantedException())
//            }
        return rxImagePicker.openGallery(context).findRotation()
    }

    fun crop(
        resultRotation: ResultRotation,
        outputMaxWidth: Int = 0,
        outputMaxHeight: Int = 0,
        outputQuality: Int = 0,
        cropMode: CropImageView.CropMode = CROP_MODE_DEFAULT
    ): Single<Bitmap> {
        return crop(
            resultRotation.uri,
            resultRotation.rotation,
            outputMaxWidth,
            outputMaxHeight,
            outputQuality,
            cropMode
        )
    }

    fun crop(
        uri: Uri,
        rotation: Int = 0,
        outputMaxWidth: Int = 0,
        outputMaxHeight: Int = 0,
        outputQuality: Int = 0,
        cropMode: CropImageView.CropMode = CROP_MODE_DEFAULT
    ): Single<Bitmap> {
        context.startActivity(
            CropActivity.getStartIntent(
                context,
                uri,
                rotation,
                outputMaxWidth,
                outputMaxHeight,
                outputQuality,
                cropMode
            )
        )
        return CropCallbackHelper.createRequest(uri.toString())
    }

    private fun Observable<Result>.findRotation(): Observable<ResultRotation> {
        return this.map {
            val uri = it.uri
            ResultRotation(uri, findImageRotation(uri))
        }
    }

    private fun findImageRotation(uri: Uri): Int {
        return try {
            with(context.contentResolver.openInputStream(uri)!!) {
                val exifInterface = ExifInterface(this)

                var rotation = 0
                val orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270
                }

                rotation
            }
        } catch (ignored: IOException) {
            0
        }
    }
}