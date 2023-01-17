package org.otunjargych.tamtam.extensions.imagepicker.ui

import android.view.View
import org.otunjargych.tamtam.extensions.imagepicker.model.Image

internal interface GalleryListener {

    // set Checked status with start Animation
    fun onChecked(image: Image)

    // Move to Detail View
    fun onClick(view: View, image: Image)

    // single selected
    fun onClick(image: Image)

    // image is already checked
    var isMultipleChecked: Boolean
}