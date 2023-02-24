package org.otunjargych.tamtam.ui.auth.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
            btnEnter.setOnClickListener {
                hideKeyboard(it)
                viewModel.login()
            }
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
            if (viewModel.fromNote) showCreateNote()
            else if (viewModel.fromProfile) showProfile()
            else showHome()
        }
    }


    private fun showGreetings(userName: String) {
        showSnackBar("Здравствуйте, $userName", R.drawable.ic_hello)
    }

    private fun showRegistration() {
        findNavController().navigate(
            R.id.register_fragment,
            bundleOf(
                "fromProfile" to viewModel.fromProfile,
                "fromNote" to viewModel.fromNote
            )
        )
    }

    private fun showHome() {
        findNavController().navigate(LoginFragmentDirections.loginToHome())
    }

    private fun showCreateNote(){
        findNavController().navigate(R.id.create_note_fragment, null, navOptions {
            popUpTo(R.id.login_fragment) { inclusive = true }
        })
    }

    private fun showProfile() {
        if (!findNavController().popBackStack(R.id.profile_fragment, false)) {
            findNavController().navigate(R.id.profile_fragment, null, navOptions {
                popUpTo(R.id.login_fragment) { inclusive = true }
            })
        }
    }

    override fun getViewModelClass(): KClass<LoginViewModel> = LoginViewModel::class
    override val layoutId: Int = R.layout.fragment_login
    override val title: CharSequence by lazy { getString(R.string.authorization) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}
}