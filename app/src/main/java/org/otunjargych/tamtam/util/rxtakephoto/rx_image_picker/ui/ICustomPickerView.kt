package org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.ui

import androidx.annotation.IdRes
import io.reactivex.Observable
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.entity.Result
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.ui.ICustomPickerConfiguration

interface ICustomPickerView {

    fun display(fragmentActivity: androidx.fragment.app.FragmentActivity,
                @IdRes viewContainer: Int,
                configuration: ICustomPickerConfiguration?)

    fun pickImage(): Observable<Result>
}