package org.otunjargych.tamtam.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentProfileSettingsBinding
import org.otunjargych.tamtam.databinding.FragmentSplashBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.ui.profileSettings.ProfileSettingsViewModel
import kotlin.reflect.KClass

class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>() {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override val layoutId: Int = R.layout.fragment_splash
    override fun getViewModelClass(): KClass<SplashViewModel> = SplashViewModel::class
}