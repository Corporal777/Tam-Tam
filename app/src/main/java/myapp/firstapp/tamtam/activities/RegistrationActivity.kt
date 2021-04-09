package myapp.firstapp.tamtam.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import myapp.firstapp.tamtam.R
import myapp.firstapp.tamtam.fragments.registration_fragments.RegistrationFragment

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportFragmentManager.beginTransaction()
            .add(R.id.registration_container, RegistrationFragment()).commit()
    }
}