package org.otunjargych.tamtam.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.otunjargych.tamtam.databinding.ActivityMainBinding
import org.otunjargych.tamtam.ui.main.MainViewModel

abstract class BaseActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplash()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun installSplash() {
        installSplashScreen().apply {
            viewModel.tokenUpdated.observe(this@BaseActivity) { updated ->
                setKeepVisibleCondition { !updated }
            }
        }
    }
}