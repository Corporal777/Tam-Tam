package org.otunjargych.tamtam.util.extensions

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView

internal enum class ImageActionState {
    NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM
}
interface OnTouchCoordinatesListener {
    fun onTouchCoordinate(view: View, event: MotionEvent, bitmapPoint: PointF)
}
interface OnTouchImageViewListener {
    fun onMove()
}
interface OnZoomFinishedListener {
    fun onZoomFinished()
}
internal data class ZoomVariables(var scale: Float, var focusX: Float, var focusY: Float, var scaleType: ImageView.ScaleType?)

enum class FixedPixel {
    CENTER, TOP_LEFT, BOTTOM_RIGHT
}