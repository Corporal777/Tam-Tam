package org.otunjargych.tamtam.util.rxtakephoto

import androidx.fragment.app.Fragment
import com.tbruyelle.rxpermissions2.RxPermissions


fun Fragment.getRxPermission(): RxPermissions {
    return RxPermissions(this)
}