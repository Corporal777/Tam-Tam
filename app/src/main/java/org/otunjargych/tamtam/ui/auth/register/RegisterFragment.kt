package org.otunjargych.tamtam.ui.auth.register

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentRegisterBinding
import org.otunjargych.tamtam.model.request.ErrorResponse.Companion.USER_ALREADY_EXISTS_ERROR
import org.otunjargych.tamtam.ui.auth.login.LoginFragmentDirections
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.util.onTextChanged
import kotlin.reflect.KClass

class RegisterFragment : BaseFragment<RegisterViewModel, FragmentRegisterBinding>(true),
    ToolbarFragment {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw { startPostponedEnterTransition() }
        observeBtnRegisterEnabled()
        observeErrors()
        observeRegisterSuccess()

        mBinding.apply {
            etFirstName.onTextChanged {
                it.toString().let { name -> viewModel.performNameChanged(name) }
            }
            etLastName.onTextChanged {
                it.toString().let { lastName -> viewModel.performLastNameChanged(lastName) }
            }
            etEmail.onTextChanged {
                it.toString().let { email -> viewModel.performEmailChanged(email) }
            }
            passwordView.setPasswordChanged { password ->
                viewModel.performPasswordChanged(password)
            }

            btnRegister.setOnClickListener { viewModel.register() }
        }
    }


    private fun observeBtnRegisterEnabled() {
        viewModel.enableBtnRegister.observe(viewLifecycleOwner) {
            mBinding.btnRegister.isEnabled = it
        }
    }

    private fun observeErrors() {
        mBinding.apply {
            viewModel.firstNameError.observe(viewLifecycleOwner) {
                if (it) tilFirstName.error = getString(R.string.empty_field)
                else tilFirstName.error = null
            }
            viewModel.lastNameError.observe(viewLifecycleOwner) {
                if (it) tilLastName.error = getString(R.string.empty_field)
                else tilLastName.error = null
            }
            viewModel.emailError.observe(viewLifecycleOwner) {
                if (it) tilEmail.error = getString(R.string.email_or_phone_incorrect)
                else tilEmail.error = null
            }
            viewModel.passwordError.observe(viewLifecycleOwner) {
                passwordView.setPasswordsNotCorrect(it)
            }
            viewModel.errorResponse.observe(viewLifecycleOwner) {
                when(it){
                    USER_ALREADY_EXISTS_ERROR -> {
                        showToast("Логин занят. Введите другой логин!")
                        mBinding.tilEmail.error = getString(R.string.login_not_free_error)
                    }
                }
            }
        }

    }

    private fun observeRegisterSuccess() {
        viewModel.successResponse.observe(viewLifecycleOwner) {
            showGreetings(it.firstName)
            showHome()
        }
    }

    private fun showGreetings(userName: String) {
        showSnackBar("Здравствуйте, $userName", R.drawable.ic_hello)
    }

    private fun showHome() {
        findNavController().navigate(RegisterFragmentDirections.registerToHome())
    }


    override val layoutId: Int = R.layout.fragment_register
    override fun getViewModelClass(): KClass<RegisterViewModel> = RegisterViewModel::class
    override val title: CharSequence by lazy { getString(R.string.registration) }
}