package org.otunjargych.tamtam.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.activities.RegistrationActivity
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.databinding.FragmentSettingsBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.viewmodel.UserViewModel

class SettingsFragment : BaseFragment() {


    private val mViewModel: UserViewModel by activityViewModels()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSnapShot: DataSnapshot


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInfo()
        initToolbarActions()
        initSettingsActions()

    }


    private fun initSettingsActions() {
        binding.apply {
            exit.setOnClickListener {
                if (hasConnection(requireContext()) && AUTH.currentUser != null) {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(activity, RegistrationActivity::class.java))
                    requireActivity().finish()
                } else {
                    toastMessage(requireContext(), getString(R.string.no_connection))
                }
            }
            writeToDev.setOnClickListener {
                val url: String =
                    "https://api.whatsapp.com/send?phone=" + "+79267806176"
                val i: Intent = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }
    }


    private fun initToolbarActions() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.change_name -> {
                    if (hasConnection(requireContext()) && AUTH.currentUser != null) {
                        replaceFragment(ChangeNameFragment())
                    } else {
                        toastMessage(requireContext(), getString(R.string.no_connection))
                    }
                }

                R.id.change_photo -> {
                    if (hasConnection(requireContext()) && AUTH.currentUser != null) {
                        val intent: Intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, 1)
                    } else {
                        toastMessage(requireContext(), getString(R.string.no_connection))
                    }
                }
            }
            true
        }
    }


    private fun initUserInfo() {
        mViewModel.currentUser.observe(viewLifecycleOwner, { user ->
            if (user != null) {
                if (!user.name.isNullOrEmpty())
                    binding.tvUserName.text = user.name + " " + user.last_name
                if (!user.phone_number.isNullOrEmpty())
                    binding.tvUserPhoneNumber.text = user.phone_number
                if (!user.email.isNullOrEmpty())
                    binding.tvUserEmail.text = user.email
                if (!user.image.isNullOrEmpty() && !user.image.equals(""))
                    binding.ivUserPhoto.load(user.image)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (hasConnection(requireContext()) && AUTH.currentUser != null) {
            mViewModel.loadCurrentUserData(USER_ID)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null && data.data != null) {
                val mData: Uri = data.data!!
                FireBaseHelper.changeUserPhoto(USER_ID, mData)
                changePhoto(mData)
            }
        }
    }


    private fun changePhoto(url: Uri) {
        binding.ivUserPhoto.load(url)
        toastMessage(requireContext(), "Фото изменено")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}