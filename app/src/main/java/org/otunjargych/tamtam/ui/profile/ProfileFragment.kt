package org.otunjargych.tamtam.ui.profile

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentProfileBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.util.setUserImage
import kotlin.reflect.KClass


class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>(), ToolbarFragment {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            tvProfileSettings.setOnClickListener {
                showProfileSettings()
            }
            tvLogout.setOnClickListener {
                if (viewModel.isUserLoggedOut()) showLogin()
                else viewModel.logout()
            }
        }
        observeUser()
        observeUserLogged()
        observeContentLoading()
    }

    private fun observeUser() {
        mBinding.apply {
            viewModel.user.observe(viewLifecycleOwner) { user ->
                lnActiveNotes.isVisible = user != null
                lnModerationNotes.isVisible = user != null
                tvUserId.isVisible = user != null
                tvCurrentTown.text = viewModel.getCurrentTown() ?: root.context.getString(R.string.not_chosen)

                tvLogout.apply {
                    text = if (viewModel.isUserLoggedOut()) getString(R.string.login_to_account)
                    else getString(R.string.logout_from_account)
                }

                if (user != null) {
                    tvUserName.text = user.nameLastName
                    tvUserId.text = user.publicId
                    ivUserPhoto.setUserImage(user.image ?: R.drawable.avatar_empty_square)
                    tvActiveNotes.text = (user.userNotes.activeNotes?.size ?: 0).toString()
                    tvModerationNotes.text = (user.userNotes.moderationNotes?.size ?: 0).toString()
                } else {
                    tvUserName.text = getString(R.string.user_unauthorized)
                    ivUserPhoto.setUserImage(R.drawable.avatar_empty_square)
                }

            }
        }
    }

    private fun observeUserLogged() {
        viewModel.successLoggedOut.observe(viewLifecycleOwner) { success ->
            if (success) {
                showSnackBar(getString(R.string.you_logged_out_from_account), R.drawable.ic_done)
                viewModel.clearSuccessLoggedOut(false)
            }
        }
    }

    private fun observeContentLoading(){
        viewModel.loadingData.observe(viewLifecycleOwner){
            mBinding.lnProfile.isVisible = !it
            val anim = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 150
            mBinding.lnProfile.startAnimation(anim)
        }
    }

    private fun showLogin() {
        findNavController().navigate(R.id.login_fragment, bundleOf("fromProfile" to true))
    }

    private fun showProfileSettings() {
        findNavController().navigate(R.id.profile_settings_fragment)
    }

    override val layoutId: Int = R.layout.fragment_profile
    override fun getViewModelClass(): KClass<ProfileViewModel> = ProfileViewModel::class
    override val title: CharSequence by lazy { getString(R.string.profile) }
}