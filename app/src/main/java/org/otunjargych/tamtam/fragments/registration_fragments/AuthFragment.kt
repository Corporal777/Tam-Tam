package org.otunjargych.tamtam.fragments.registration_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.activities.MainActivity
import org.otunjargych.tamtam.databinding.FragmentAuthBinding
import org.otunjargych.tamtam.extensions.AUTH
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.extensions.toastMessage

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
        if (!phoneNumber.isNullOrEmpty() || !password.isNullOrEmpty()) {
            AUTH.signInWithEmailAndPassword(phoneNumber, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    toastMessage(requireContext(), "Добро пожаловать")
                    replaceActivity()
                }
            }.addOnFailureListener {
                toastMessage(requireContext(), "Такого аккаунта не существует!")
            }
        } else toastMessage(requireContext(), "Заполните все поля ввода!")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }

}