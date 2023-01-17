package org.otunjargych.tamtam.activities

import android.os.Bundle
import androidx.fragment.app.commit
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ActivityRegistrationBinding
import org.otunjargych.tamtam.extensions.BaseActivity
import org.otunjargych.tamtam.fragments.registration_fragments.RegistrationFragment


class RegistrationActivity : BaseActivity() {


    private lateinit var binding: ActivityRegistrationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)


        setContentView(binding.root)
       if (savedInstanceState == null){
           supportFragmentManager.commit {
               setReorderingAllowed(true)
               add(R.id.registration_container, RegistrationFragment())
           }
       }

    }


}