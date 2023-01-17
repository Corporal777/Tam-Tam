package org.otunjargych.tamtam.fragments.registration_fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.activities.MainActivity
import org.otunjargych.tamtam.databinding.FragmentRegistrationBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.model.User


class RegistrationFragment : BaseFragment() {

    private lateinit var mPhoneNumber: String
    private lateinit var mPassword: String
    private lateinit var mName: String
    private lateinit var mLastName: String
    private lateinit var mEmail: String
    private lateinit var mUserId: String
    private lateinit var mRefUsers: DatabaseReference

    private var mUserPhoto: Uri? = null
    private var mImageUrl: String = ""

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

        binding.tvAccAlreadyExists.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.registration_container, AuthFragment())
            }
        }
        Boom(binding.btnAccept)
        binding.btnAccept.setOnClickListener {
            if (hasConnection(requireContext())){
                binding.progressView.isVisible = true
                authUser()
            }else{
                toastMessage(requireContext(), getString(R.string.no_connection))
            }


        }
        Boom(binding.userPhoto)
        binding.userPhoto.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                binding.userPhoto.load(data.data)
                mUserPhoto = data.data
            }
        }
    }

    private fun authUser() {
        mPhoneNumber = binding.etPhone.text.toString() + "@gmail.com"
        mPassword = binding.etPassword.text.toString()

        AUTH = FirebaseAuth.getInstance()
        AUTH.createUserWithEmailAndPassword(mPhoneNumber, mPassword)
            .addOnSuccessListener {
                initUserParams()
            }.addOnFailureListener {
                toastMessage(requireContext(), "Что-то пошло не так!")
            }.addOnCompleteListener {

            }
        AUTH.signInWithEmailAndPassword(mPhoneNumber, mPassword).addOnCompleteListener {
            toastMessage(requireContext(), "Добро пожаловать")
            addUser()
            binding.progressView.isVisible = false
            replaceActivity()
        }.addOnFailureListener {
            toastMessage(requireContext(), "Что-то пошло не так!")
        }

    }


    private fun addUser() {
        REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
        if (mUserPhoto != null) {
            val path =
                REF_STORAGE_ROOT.child(FOLDER_USER_IMAGES).child(mUserId)
            path.putFile(mUserPhoto!!).addOnCompleteListener {
                if (it.isSuccessful) {
                    path.downloadUrl.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            mImageUrl = task.result.toString()
                            createUser()
                        }
                    }
                }
            }
        } else {
            createUser()
        }
    }


    private fun initUserParams() {
        mPhoneNumber = binding.etPhone.text.toString()
        mPassword = binding.etPassword.text.toString()
        mEmail = binding.etEmail.text.toString()
        mName = binding.etName.text.toString()
        mLastName = binding.etLastName.text.toString()
        mUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
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

    }

    private fun createUser() {
        val user =
            User(
                mUserId,
                mName,
                mLastName,
                mPhoneNumber,
                mEmail,
                mImageUrl
            )
        Firebase.firestore.collection(NODE_USERS)
            .document(mUserId)
            .set(user)
    }
}
