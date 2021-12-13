package org.otunjargych.tamtam.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import org.otunjargych.tamtam.R


fun Fragment.replaceFragment(fragment: Fragment) {
    requireActivity().supportFragmentManager.commit {

        //setReorderingAllowed(true)
        replace(R.id.fragment_container, fragment)
        addToBackStack(null)
    }
}


fun AppCompatActivity.replaceFragment(fragment: Fragment) {
    supportFragmentManager.beginTransaction()
        ?.replace(R.id.fragment_container, fragment)?.addToBackStack(null)?.commit()

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

fun errorToast(message: String, context: Context) {
    val layout: View = LayoutInflater.from(context).inflate(R.layout.custom_error_toast, null)
    val mTextView: TextView = layout.findViewById(R.id.toast_message)
    val toast: Toast = Toast(context)
    mTextView.text = message
    toast.view = layout
    toast.duration = Toast.LENGTH_LONG
    toast.show()

}

fun successToast(message: String, context: Context) {
    val layout: View = LayoutInflater.from(context).inflate(R.layout.custom_success_toast, null)
    val mTextView: TextView = layout.findViewById(R.id.toast_message)
    val toast: Toast = Toast(context)
    mTextView.text = message
    toast.view = layout
    toast.duration = Toast.LENGTH_LONG
    toast.show()
}

@SuppressLint("ResourceAsColor")
fun errorConnection(view: View) {
    Snackbar.make(view, "Нет интернет соединения!", Snackbar.LENGTH_LONG)
        .setBackgroundTint(R.color.app_main_color).show()
}

