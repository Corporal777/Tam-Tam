package org.otunjargych.tamtam.ui.profileSettings.changeLogin

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.BottomSheetChangeLoginBinding
import org.otunjargych.tamtam.databinding.BottomSheetChangePasswordBinding
import org.otunjargych.tamtam.ui.base.BaseBottomSheetFragment
import org.otunjargych.tamtam.ui.profileSettings.changePassword.ChangePasswordFragment
import org.otunjargych.tamtam.ui.profileSettings.changePassword.ChangePasswordViewModel
import org.otunjargych.tamtam.util.AuthValidateUtil
import org.otunjargych.tamtam.util.isPhone
import org.otunjargych.tamtam.util.isPhoneIsValid
import org.otunjargych.tamtam.util.onTextChanged
import kotlin.reflect.KClass

class ChangeLoginFragment :
    BaseBottomSheetFragment<ChangeLoginViewModel, BottomSheetChangeLoginBinding>() {


    private var onSaveNewLogin: (login: String) -> Unit = {}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            etLogin.onTextChanged {
                viewModel.performChangeLogin(it.toString())
            }
            btnSave.setOnClickListener {
                viewModel.updateLogin()
            }
        }
        observeLoginError()
        observeLoginUpdated()
    }


    private fun observeLoginError() {
        viewModel.loginError.observe(viewLifecycleOwner) {
            if (it == null) mBinding.tilLogin.error = null
            else mBinding.tilLogin.error = getString(it)
        }
    }

    private fun observeLoginUpdated() {
        viewModel.loginUpdated.observe(viewLifecycleOwner){
            if (it){
                onSaveNewLogin.invoke(mBinding.etLogin.text.toString())
                dismiss()
                showSnackBar(getString(R.string.login_success_changed), R.drawable.ic_done)
            }
        }
    }

    fun setLoginCallback(block: (password: String) -> Unit): ChangeLoginFragment {
        onSaveNewLogin = block
        return this
    }

    override val layoutId: Int = R.layout.bottom_sheet_change_login
    override fun getViewModelClass(): KClass<ChangeLoginViewModel> = ChangeLoginViewModel::class
}