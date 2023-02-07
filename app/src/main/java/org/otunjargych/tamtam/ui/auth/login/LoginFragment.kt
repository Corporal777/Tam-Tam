package org.otunjargych.tamtam.ui.auth.login

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentLoginBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.util.onTextChanged
import kotlin.reflect.KClass

class LoginFragment : BaseFragment<LoginViewModel, FragmentLoginBinding>(), ToolbarFragment {


    private val args: LoginFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            viewModel.fromNote = LoginFragmentArgs.fromBundle(it).fromNote
            viewModel.fromProfile = LoginFragmentArgs.fromBundle(it).fromProfile
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            etLogin.onTextChanged { viewModel.performChangeLogin(it.toString()) }
            etPassword.onTextChanged { viewModel.performChangePassword(it.toString()) }
            btnEnter.setOnClickListener { viewModel.login() }
            btnRegister.setOnClickListener { showRegistration() }
        }
        observeErrors()
        observeResponses()
    }

    private fun observeErrors() {
        mBinding.apply {
            viewModel.loginError.observe(viewLifecycleOwner) {
                if (it) mBinding.tilLogin.error = getString(R.string.login_incorrect)
                else mBinding.tilLogin.error = null
            }
            viewModel.passwordError.observe(viewLifecycleOwner) {
                if (it) mBinding.tilPassword.error = getString(R.string.password_incorrect)
                else mBinding.tilPassword.error = null
            }
            viewModel.enableBtnLogin.observe(viewLifecycleOwner) {
                mBinding.btnEnter.isEnabled = it
            }
        }
    }

    private fun observeResponses() {
        viewModel.errorResponse.observe(viewLifecycleOwner) {
            if (it) showToast("Не правильный логин или пароль!")
        }
        viewModel.successResponse.observe(viewLifecycleOwner) {
            showGreetings(it.firstName)
            if (viewModel.fromNote)
            else if (viewModel.fromProfile) showProfile()
            else showHome()
        }
    }

    private fun showGreetings(userName: String) {
        showSnackBar("Здравствуйте, $userName", R.drawable.ic_hello)
    }

    private fun showRegistration() {
        findNavController().navigate(LoginFragmentDirections.loginToRegister())
    }

    private fun showHome() {
        findNavController().navigate(LoginFragmentDirections.loginToHome())
    }

    private fun showProfile() {
        findNavController().navigate(LoginFragmentDirections.loginToProfile())
    }

    override fun getViewModelClass(): KClass<LoginViewModel> = LoginViewModel::class
    override val layoutId: Int = R.layout.fragment_login
    override val title: CharSequence by lazy { getString(R.string.authorization) }
}