package org.otunjargych.tamtam.di.module

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.data.AppPrefs
import org.otunjargych.tamtam.di.data.AppPrefsImpl

fun provideFusedLocationClient(context: Context) =
    LocationServices.getFusedLocationProviderClient(context)

val locationModule = module {
    single { provideFusedLocationClient(androidContext()) }
}