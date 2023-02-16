package org.otunjargych.tamtam.ui.profileSettings.changePassword

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.BottomSheetChangePasswordBinding
import org.otunjargych.tamtam.databinding.FragmentMetroStationsBinding
import org.otunjargych.tamtam.ui.base.BaseBottomSheetFragment
import org.otunjargych.tamtam.ui.location.MetroStationsFragment
import org.otunjargych.tamtam.ui.location.MetroStationsViewModel
import org.otunjargych.tamtam.ui.location.items.MetroStationItem
import org.otunjargych.tamtam.ui.views.SimpleTextWatcher
import org.otunjargych.tamtam.util.onTextChanged
import kotlin.reflect.KClass

class ChangePasswordFragment :
    BaseBottomSheetFragment<ChangePasswordViewModel, BottomSheetChangePasswordBinding>() {


    private var onSaveNewPassword: (password: String) -> Unit = {}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }
            etCurrentPassword.onTextChanged {
                viewModel.performChangeCurrentPassword(it.toString())
            }
            passwordView.setPasswordChanged { password ->
                viewModel.performChangeNewPassword(password)
            }
        }
        observeCurrentPassword()
        observeCurrentPasswordError()
        observePasswordUpdated()
    }

    private fun observeCurrentPassword() {
        viewModel.currentPasswordCorrect.observe(viewLifecycleOwner) { correct ->
            mBinding.apply {
                lnCurrentPassword.isVisible = !correct
                lnNewPassword.isVisible = correct

                btnSave.apply {
                    if (correct) {
                        text = getString(R.string.save)
                        setOnClickListener {
                            viewModel.updateUserPassword()
                        }
                    } else {
                        text = getString(R.string.do_continue)
                        setOnClickListener {
                            viewModel.checkCurrentPasswordIsCorrect()
                        }
                    }
                }
            }

        }
    }

    private fun observeCurrentPasswordError(){
        viewModel.passwordError.observe(viewLifecycleOwner){
            if (it) mBinding.tilCurrentPassword.error = getString(R.string.password_incorrect)
            else mBinding.tilCurrentPassword.error = null
            mBinding.passwordView.setPasswordsNotCorrect(it)
        }
    }

    private fun observePasswordUpdated(){
        viewModel.passwordSuccessUpdated.observe(viewLifecycleOwner){
            if (it){
                dismiss()
                showSnackBar("Пароль успешно изменен", R.drawable.ic_done)
            }
        }
    }

    fun setPasswordCallback(block: (password: String) -> Unit): ChangePasswordFragment {
        onSaveNewPassword = block
        return this
    }

    override val layoutId: Int = R.layout.bottom_sheet_change_password
    override fun getViewModelClass(): KClass<ChangePasswordViewModel> =
        ChangePasswordViewModel::class
}