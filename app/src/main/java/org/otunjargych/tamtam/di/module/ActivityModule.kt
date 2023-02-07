package org.otunjargych.tamtam.di.module

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.tbruyelle.rxpermissions2.RxPermissions
import org.koin.dsl.module
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.ui.main.MainActivity
import org.otunjargych.tamtam.util.rxtakephoto.RxTakePhoto


fun provideMainActivity(): MainActivity = MainActivity.getInstance()!!
fun provideRxTakePhoto(activity: MainActivity) = RxTakePhoto(activity)
fun provideRxPermissions(activity: MainActivity): RxPermissions = RxPermissions(activity)

val activityModule = module {
    single { provideMainActivity() }
    single { provideRxTakePhoto(get()) }
    single { provideRxPermissions(get()) }
}