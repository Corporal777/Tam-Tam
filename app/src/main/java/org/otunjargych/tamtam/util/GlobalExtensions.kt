package org.otunjargych.tamtam.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnDetach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.scope.lifecycleScope
import org.koin.core.definition.BeanDefinition
import org.koin.core.definition.IndexKey
import org.koin.core.instance.InstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.scope.Scope
import org.otunjargych.tamtam.R
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
    onViewCreated: (f: Fragment) -> Unit,
    onViewDestroyed: (f: Fragment) -> Unit,
    onFragmentStarted: (f: Fragment) -> Unit,
    onFragmentStopped: (f: Fragment) -> Unit
): FragmentManager.FragmentLifecycleCallbacks {
    val callback = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            onViewDestroyed(f)
        }

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            onViewCreated(f)
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            super.onFragmentStarted(fm, f)
            onFragmentStarted(f)
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            onFragmentStopped(f)
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
    window.statusBarColor = window.context.getColor(R.color.app_main_color)
    InsetUtil.returnSystemInsets(window.decorView, listener)
}

fun Activity.doEdgeWindow(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    //    window.navigationBarColor = Color.TRANSPARENT
    window.statusBarColor = Color.TRANSPARENT
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

fun List<Bitmap>.toBodyPart(
    name: String,
    fileName: String,
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
): MultipartBody {
    return MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .apply {
            forEachIndexed { index, bitmap ->
                val byteArray = ByteArrayOutputStream().let {
                    bitmap.compress(compressFormat, 100, it)
                    it.toByteArray()
                }

                val body = byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                addFormDataPart(name + index.toString(), fileName + index.toString(), body)
            }
        }.build()
}


fun String?.parseColor(): Int? {
    if (this == null || isEmpty()) return null

    return try {
        Color.parseColor(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun showPopupMenu(view: View, list: List<PowerMenuItem>, block: (item: PowerMenuItem) -> Unit) {
//    val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.CustomPopupMenuStyle)
//    val popupMenu = PopupMenu(requireContext(), view, R.style.CustomPopupMenuStyle)

//        popupMenu.apply {
//            setForceShowIcon(true)
//            setOnMenuItemClickListener { item ->
//                when (item?.itemId) {
//
//                    R.id.change_name -> {
//                        true
//                    }
//                    else -> false
//                }
//            }
//            inflate(R.menu.account_menu)
//            show()
//        }

    val popup = PowerMenu.Builder(view.context)
        .addItemList(list)
        .setAutoDismiss(true)
        .setShowBackground(false)
        .setAnimation(MenuAnimation.DROP_DOWN)
        .setMenuRadius(15f)
        .setMenuShadow(10f)
        .setTextColor(ContextCompat.getColor(view.context, R.color.black))
        .setTextSize(16)
        .setTextGravity(Gravity.START)
        .setTextTypeface(Typeface.createFromAsset(view.context.assets, "noto_sans_medium.ttf"))
        .setWidth(670)
        .setIconSize(25)

        .setMenuColor(Color.WHITE)
        .setOnMenuItemClickListener { position, item ->
            block.invoke(item)
        }
        .build()

    popup.showAsDropDown(view)
    view.doOnDetach {
        popup.dismiss()
    }
}



