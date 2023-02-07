package org.otunjargych.tamtam.ui.profileSettings

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentProfileSettingsBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.ui.views.dialogs.ChangePhotoBottomSheetDialog
import org.otunjargych.tamtam.util.onTextChanged
import org.otunjargych.tamtam.util.setUserImage
import kotlin.reflect.KClass

class ProfileSettingsFragment :
    BaseFragment<ProfileSettingsViewModel, FragmentProfileSettingsBinding>(true),
    ToolbarFragment {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnLayout { startPostponedEnterTransition() }
        mBinding.apply {
            etFirstName.onTextChanged { text ->
                viewModel.performChangeFirstName(text.toString())
            }
            etLastName.onTextChanged { text ->
                viewModel.performChangeLastName(text.toString())
            }
            etLogin.onTextChanged { text ->
                viewModel.performChangeLogin(text.toString())
            }
            etPassword.onTextChanged { text ->
                viewModel.performChangePassword(text.toString())
            }
            etEmail.onTextChanged { text ->
                viewModel.performChangeEmail(text.toString())
            }
            etPhone.onTextChanged { text ->
                viewModel.performChangePhone(text.toString())
            }
            btnChangeImage.setOnClickListener {
                showChangePhotoDialog()
            }
            btnSave.setOnClickListener {
                viewModel.updateUser()
            }
        }
        observeUser()
        observeBtnSaveEnable()
    }

    private fun observeUser() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                mBinding.apply {
                    ivUserPhoto.setUserImage(user.image ?: R.drawable.avatar_empty_square)
                    etFirstName.setText(user.firstName)
                    etLastName.setText(user.lastName)
                    etLogin.setText(user.login)
                    etPassword.setText("password")
                    etEmail.setText(user.contactEmail)
                    etPhone.setText(user.contactPhone)
                }
            }
        }
    }

    private fun observeBtnSaveEnable() {
        viewModel.enableBtnSave.observe(viewLifecycleOwner) {
            mBinding.btnSave.isEnabled = it
        }
    }

    private fun showChangePhotoDialog() {
        val changePhotoDialog = ChangePhotoBottomSheetDialog()
        changePhotoDialog.show(requireActivity().supportFragmentManager, "change_photo_dialog")
        changePhotoDialog.setActionCameraCallback {
            viewModel.takeCameraImage()
        }
        changePhotoDialog.setActionGalleryCallback {
            viewModel.takeGalleryImage()
        }
    }

    override val layoutId: Int = R.layout.fragment_profile_settings
    override fun getViewModelClass(): KClass<ProfileSettingsViewModel> =
        ProfileSettingsViewModel::class

    override val title: CharSequence by lazy { getString(R.string.profile_settings) }
}