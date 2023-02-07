package org.otunjargych.tamtam.di.module

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.data.AppPrefs
import org.otunjargych.tamtam.di.data.AppPrefsImpl
import retrofit2.Retrofit

val appDataModule = module {

    fun provideAppPrefs(context: Context): AppPrefs = AppPrefsImpl(context)
    fun provideAppData(appPrefs: AppPrefs): AppData = AppData(appPrefs)

    single { provideAppPrefs(androidContext()) }
    single { provideAppData(get()) }
}