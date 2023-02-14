package org.otunjargych.tamtam.util

import android.content.res.Resources

val Float.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)
val Float.sp: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity + 0.5f)
val Float.px: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

val Int.dp: Int
    get() = this.toFloat().dp.toInt()
val Int.sp: Int
    get() = this.toFloat().sp.toInt()
val Int.px: Int
    get() = this.toFloat().px.toInt()

fun isPhoneIsValid(phone: String?): Boolean {
    var valid = true
    if (!phone.isNullOrEmpty()) {
        if (phone.contains("+")) {
            if (phone.length == 12) {
                if (phone.substring(0, 3) != "+79") valid = false
            } else valid = false
        } else {
            if (phone.length == 11) {
                val firstNumber = phone.substring(0, 2)
                if (firstNumber != "79" && firstNumber != "89") valid = false
            } else valid = false
        }
    } else {
        valid = false
    }
    return valid
}

fun isPhone(text: String?): Boolean {
    if (text.isNullOrEmpty()) return false
    val regex = Regex(pattern = "[0-9]+")
    return regex.containsMatchIn(text)
}

fun isContainLetters(text: String?): Boolean {
    if (text.isNullOrEmpty()) return false
    val regex = Regex(pattern = "[A-Za-z]+")
    return regex.containsMatchIn(text)
}
