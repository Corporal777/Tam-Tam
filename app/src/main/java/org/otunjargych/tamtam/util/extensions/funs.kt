package org.otunjargych.tamtam.util.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.util.extensions.imagepicker.ui.ImagePickerView
import java.text.SimpleDateFormat
import java.util.*


fun Fragment.replaceFragment(fragment: Fragment) {

}


fun AppCompatActivity.replaceFragment(fragment: Fragment) {

}

fun hasConnection(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    if (wifiInfo != null && wifiInfo.isConnected) {
        return true
    }
    wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    if (wifiInfo != null && wifiInfo.isConnected) {
        return true
    }
    wifiInfo = cm.activeNetworkInfo
    if (wifiInfo != null && wifiInfo.isConnected) {
        return true
    } else return false
}


@SuppressLint("ResourceAsColor")
fun snackMessage(context: Context, view: View?, message: String) {

    Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).also { snackbar ->
        snackbar.setBackgroundTint(context.resources.getColor(R.color.app_main_color))
        snackbar.setActionTextColor(context.resources.getColor(R.color.white))
        val textview =
            snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textview.textSize = 16F
        val font = Typeface.createFromAsset(context.assets, "commons_medium.ttf")
        textview.typeface = font
    }.show()

}

fun toastMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


fun Long.calendar(): Calendar = Calendar.getInstance().apply { timeInMillis = this@calendar }

fun getCategoriesList(): List<String> {
    val categoriesList = ArrayList<String>()
    categoriesList.add("Работа, Вакансии")
    categoriesList.add("Транспорт, Перевозки")
    categoriesList.add("Медицина, Красота")
    categoriesList.add("Продажа, Покупка")
    categoriesList.add("Квартиры, Гостиницы")
    categoriesList.add("Обучение, Услуги")
    return categoriesList
}

fun getWorkSchedulesList() : MutableList<String>{
    return mutableListOf<String>(
        "Полный день",
        "Не полный день",
        "Свободный график",
        "Сменный график",
        "Гибкий график",
        "Без выходных",
        "5/2",
        "6/1",
        "Вахта"
    )
}

fun getWorkSubCategoriesList() : MutableList<String>{
    return mutableListOf<String>(
        "Вакансия",
        "Подработка",
        "Стажировка",
        "Ищу работу",
        "Ищу подработку"
    )
}

fun getEmptyImage(): String {
    return "https://firebasestorage.googleapis.com/v0/b/tam-tam-8b2a7.appspot.com/o/notes_images%2Fplaceholder.png?alt=media&token=c8c79ca4-a95c-465e-9b06-f0c7d6ed5c91"
}

fun Fragment.openImagePicker() {
    ImagePickerView.Builder()
        .setup {
            name { RESULT_NAME }
            max { 6 }
            title { "Галлерея" }
            single { false }
        }
        .start(this)
}

interface OnBottomAppBarStateChangeListener {
    fun onHide()
    fun onShow()
}

interface OnBottomAppBarItemsEnabledListener {
    fun enabledHomeItem()
    fun enabledSettingsItem()
    fun enabledLikedItem()
    fun disabledHomeItem()
    fun disabledSettingsItem()
    fun disabledLikedItem()
}

fun Fragment.hideKeyboard(view: View) {
    view.clearFocus()
    val inn: InputMethodManager? =
        requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
    inn?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun onCompareText(note: String, word: String): Boolean {
    return (note.contains(word, true) || note.contentEquals(word, true))
}

fun isSimilar(node: Node, str: String): Boolean {
    return node.text.contains(str, true) || node.title.contains(str, true) || node.station.contains(
        str,
        true
    )
}

fun onCompareTitle(note: String, word: String): Boolean {
    return (note.contains(word, true) || note.contentEquals(word, true))
}


class AppTextWatcher(val onSuccess: (CharSequence?) -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        onSuccess(s)
    }

    override fun afterTextChanged(s: Editable?) {

    }
}

interface OnNodeClickListener {
    fun onNodeClick(node: Node, position: Int)
}




