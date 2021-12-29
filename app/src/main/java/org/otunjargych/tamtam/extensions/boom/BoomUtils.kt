package org.otunjargych.tamtam.extensions.boom

import android.os.Build
import android.view.View

object BoomUtils {

    fun boomAll(vararg views: View) {
        views.forEach { Boom(it) }
    }

}

fun View.boom(withRipple: Boolean? = false) {
    Boom(this)
    if (withRipple == true && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) this.applyRipple()
}

fun View.removeBoom() {
    this.setOnTouchListener(null)
}