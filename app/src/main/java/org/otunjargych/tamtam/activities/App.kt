package org.otunjargych.tamtam.activities

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import org.otunjargych.tamtam.extensions.YANDEX_APP_METRIC

class App : Application() {

    override fun onCreate() {
        super.onCreate()
	    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        FirebaseApp.initializeApp(applicationContext)
        initYandexAppMetric()
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
//        FirebaseDatabase.getInstance().reference.keepSynced(true)

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        Firebase.firestore.firestoreSettings = settings


    }


    private fun initYandexAppMetric() {
        val config = YandexMetricaConfig.newConfigBuilder(YANDEX_APP_METRIC).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }


}
