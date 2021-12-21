package org.otunjargych.tamtam.fragments.registration_fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.activities.MainActivity
import org.otunjargych.tamtam.databinding.FragmentRegistrationBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.model.User


class RegistrationFragment : BaseFragment() {

    private lateinit var mPhoneNumber: String
    private lateinit var mPassword: String
    private lateinit var mName: String
    private lateinit var mLastName: String
    private lateinit var mEmail: String
    private lateinit var mRefUsers: DatabaseReference

    private var userPhoto: Uri? = null
    private var imageUrl: String = ""

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarAuth.customTitle.text = getString(R.string.authorization)

        binding.apply {
            tvAccAlreadyExists.setOnClickListener {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.registration_container, AuthFragment())?.commit()
            }
            btnAccept.setOnClickListener {
                authUser()
            }
            userPhoto.setOnClickListener {
                val intent: Intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                binding.userPhoto.load(data.data)
                userPhoto = data.data


            }
        }
    }

    private fun authUser() {

        mPhoneNumber = binding.etPhone.text.toString() + "@gmail.com"
        mPassword = binding.etPassword.text.toString()

        AUTH = FirebaseAuth.getInstance()
        AUTH.createUserWithEmailAndPassword(mPhoneNumber, mPassword).addOnCompleteListener {}
            .addOnSuccessListener {
                addUser()
                successToast("Добро пожаловать!", activity!!)
                startActivity(
                    Intent(
                        requireActivity(),
                        MainActivity::class.java
                    )
                )
                requireActivity().finish()
            }.addOnFailureListener {
                errorToast("Профиль не создан!", activity!!)
            }

        AUTH.signInWithEmailAndPassword(mPhoneNumber, mPassword)

    }


    private fun addUser() {

        mPhoneNumber = binding.etPhone.text.toString()
        mPassword = binding.etPassword.text.toString()
        mEmail = binding.etEmail.text.toString()
        mName = binding.etName.text.toString()
        mLastName = binding.etLastName.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        mRefUsers = FirebaseDatabase.getInstance().reference

        REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
        val path = REF_STORAGE_ROOT.child(FOLDER_USER_IMAGES).child(uid)
        if (userPhoto != null) {
            path.putFile(userPhoto!!).addOnCompleteListener {
                if (it.isSuccessful) {
                    path.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            imageUrl = task.result.toString()
                            val user: User =
                                User(uid, mName, mLastName, mPhoneNumber, mEmail, imageUrl)
                            mRefUsers.child(NODE_USERS).child(uid).setValue(user)
                        }
                    }
                }
            }
        } else {
            val user: User = User(uid, mName, mLastName, mPhoneNumber, mEmail, imageUrl)
            mRefUsers.child(NODE_USERS).child(uid).setValue(user)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
