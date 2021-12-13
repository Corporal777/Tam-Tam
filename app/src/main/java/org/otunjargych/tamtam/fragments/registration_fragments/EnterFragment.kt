package org.otunjargych.tamtam.fragments.registration_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.extensions.AUTH
import org.otunjargych.tamtam.extensions.errorToast
import org.otunjargych.tamtam.extensions.successToast

class EnterFragment() : Fragment() {

    private lateinit var mEditTextPhoneNumber: EditText
    private lateinit var mEditTextPassword: EditText
    private lateinit var mButtonEnter: MaterialButton
    private lateinit var mTextViewReg: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_code_reg, container, false)
        mEditTextPhoneNumber = view.findViewById(R.id.et_number_phone_auth)
        mEditTextPassword = view.findViewById(R.id.et_password_auth)
        mButtonEnter = view.findViewById(R.id.btn_enter)
        mTextViewReg = view.findViewById(R.id.tv_registration)

        mButtonEnter.setOnClickListener {
            authUser()
        }

        mTextViewReg.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.registration_container, RegistrationFragment())?.commit()
        }

        return view
    }


    private fun authUser() {
        val phone_number: String = mEditTextPhoneNumber.text.toString() + "@gmail.com"
        val password: String = mEditTextPassword.text.toString()
        AUTH = FirebaseAuth.getInstance()
        AUTH.signInWithEmailAndPassword(phone_number, password).addOnCompleteListener {
            if (it.isSuccessful) {
                successToast("Добро пожаловать!", activity!!)
                startActivity(Intent(activity, org.otunjargych.tamtam.activities.MainActivity::class.java))
                activity?.finish()
            }
        }.addOnFailureListener {
            errorToast("Такого аккаунта не существует!", activity!!)
        }
    }

}