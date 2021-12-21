package org.otunjargych.tamtam.fragments.registration_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.activities.MainActivity
import org.otunjargych.tamtam.databinding.FragmentAuthBinding
import org.otunjargych.tamtam.extensions.AUTH
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.extensions.errorToast
import org.otunjargych.tamtam.extensions.successToast

class AuthFragment() : BaseFragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        binding.authToolbar.customTitle.text = getString(R.string.authorization)

        binding.btnEnter.setOnClickListener {
            authUser()
        }

        binding.tvRegistration.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.registration_container, RegistrationFragment()).commit()
        }

        return binding.root
    }


    private fun authUser() {

        val phoneNumber: String = binding.etNumberPhoneAuth.text.toString() + "@gmail.com"
        val password: String = binding.etPasswordAuth.text.toString()

        AUTH = FirebaseAuth.getInstance()
        if (!phoneNumber.isNullOrEmpty() && !password.isNullOrEmpty()) {
            AUTH.signInWithEmailAndPassword(phoneNumber, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    successToast("Добро пожаловать!", activity!!)
                    startActivity(
                        Intent(
                            requireActivity(),
                            MainActivity::class.java
                        )
                    )
                    requireActivity().finish()
                }
            }.addOnFailureListener {
                errorToast("Такого аккаунта не существует!", activity!!)
            }
        } else Toast.makeText(requireContext(), "Заполните все поля ввода!", Toast.LENGTH_SHORT)
            .show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}