package org.otunjargych.tamtam.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.scope.lifecycleScope
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.IndexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.scope.Scope
import java.io.ByteArrayOutputStream


fun onPageChanged(onPageChanged: (position: Int) -> Unit): ViewPager.SimpleOnPageChangeListener {
    val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            onPageChanged(position)
        }
    }
    return pageChangeListener
}

fun getFragmentLifecycleCallback(
    onViewCreated: (f: Fragment, ) -> Unit,
    onViewDestroyed: (f: Fragment, ) -> Unit
): FragmentManager.FragmentLifecycleCallbacks {
    val callback = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            onViewDestroyed(f)
        }
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            onViewCreated(f)
        }
    }
    return callback
}




typealias OnSystemInsetsChangedListener = (statusBarSize: Int, navigationBarSize: Int) -> Unit

object InsetUtil {

    fun doEdgeDisplay(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, 0)
            )
        }
    }

    fun removeSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom)
            )
        }
    }

    fun returnSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(
                    0,
                    insets.systemWindowInsetTop,
                    0,
                    insets.systemWindowInsetBottom,
                )
            )
        }
    }

}

fun Activity.setWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.removeSystemInsets(window.decorView, listener)
//    window.navigationBarColor = Color.TRANSPARENT
//    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.cancelWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.returnSystemInsets(window.decorView, listener)
}

fun Activity.doEdgeWindow(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.doEdgeDisplay(window.decorView, listener)
}

fun Bitmap.toBodyPart(
    name: String,
    fileName: String,
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
): MultipartBody.Part {
    return let { bitmap ->
        val byteArray = ByteArrayOutputStream().let {
            bitmap.compress(compressFormat, 100, it)
            it.toByteArray()
        }

        val body = byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(name, fileName, body)
    }
}


fun String?.parseColor(): Int? {
    if (this == null || isEmpty()) return null

    return try {
        Color.parseColor(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}



