package org.otunjargych.tamtam

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.otunjargych.tamtam.di.module.*
import org.otunjargych.tamtam.util.extensions.YANDEX_APP_METRIC

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(applicationContext)
            modules(
                listOf(
                    viewModelModule, remoteDataSourceModule, repoModule, appDataModule,
                    activityModule, locationModule
                )
            )
        }
        context = applicationContext

        FirebaseApp.initializeApp(applicationContext)
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        Firebase.firestore.firestoreSettings = settings

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
//        FirebaseDatabase.getInstance().reference.keepSynced(true)


        //initYandexAppMetric()
    }


    private fun initYandexAppMetric() {
        val config = YandexMetricaConfig.newConfigBuilder(YANDEX_APP_METRIC).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }

    companion object {
        @JvmStatic
        var context: Context? = null
            private set
    }

}
