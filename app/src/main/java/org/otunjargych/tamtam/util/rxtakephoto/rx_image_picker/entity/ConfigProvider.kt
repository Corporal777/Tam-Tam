package org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.entity
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.entity.sources.SourcesFrom
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.ui.ICustomPickerConfiguration
import org.otunjargych.tamtam.util.rxtakephoto.rx_image_picker.ui.ICustomPickerView
import kotlin.reflect.KClass

data class ConfigProvider(val componentClazz: KClass<*>,
                          val asFragment: Boolean,
                          val sourcesFrom: SourcesFrom,
                          @param:IdRes val containerViewId: Int,
                          val fragmentActivity: FragmentActivity,
                          val pickerView: ICustomPickerView,
                          val config: ICustomPickerConfiguration?)