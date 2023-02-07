package org.otunjargych.tamtam.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import org.otunjargych.tamtam.databinding.ActivitySplashBinding
import org.otunjargych.tamtam.util.extensions.BaseActivity
import org.otunjargych.tamtam.ui.main.MainActivity


class SplashActivity : org.otunjargych.tamtam.util.extensions.BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openMain()
    }


    private fun openMain() {
        mHandler = Handler()
        mRunnable = Runnable {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
        mHandler.postDelayed(mRunnable, 500)
    }
}