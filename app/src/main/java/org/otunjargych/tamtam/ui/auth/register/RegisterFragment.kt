package org.otunjargych.tamtam.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentRegisterBinding
import org.otunjargych.tamtam.model.request.ErrorResponse.Companion.USER_ALREADY_EXISTS_ERROR
import org.otunjargych.tamtam.ui.auth.login.LoginFragmentDirections
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.ui.views.dialogs.ActionsMessageDialog
import org.otunjargych.tamtam.util.onTextChanged
import kotlin.reflect.KClass

class RegisterFragment : BaseFragment<RegisterViewModel, FragmentRegisterBinding>(true),
    ToolbarFragment {


    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw { startPostponedEnterTransition() }
        view.doOnLayout { showUseGoogleAccountDataDialog() }
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
                when (it) {
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

    private fun showUseGoogleAccountDataDialog() {
        ActionsMessageDialog(
            requireContext(),
            "Использовать данные Google аккаунта?",
            "Поля ввода для регистрации будут заполнены данными вашего аккаунта. Приложение не получает доступ к паролю аккаунта."
        ).setAcceptCallback {
            showGoogleSignIn()
        }
    }

    private fun showGoogleSignIn() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        if (account != null) {
            setGoogleAccountData(account)
        } else {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnCompleteListener {
                    if (it.isSuccessful && it.result != null) {
                        setGoogleAccountData(it.result)
                    } else {
                        it.exception?.printStackTrace()
                    }
                }
        }
    }

    private fun setGoogleAccountData(account: GoogleSignInAccount) {
        val name = account.givenName ?: ""
        val lastName = account.familyName ?: ""
        val email = account.email ?: ""
        viewModel.setFieldsWithGoogleAccountData(name, lastName, email)
        mBinding.apply {
            etFirstName.setText(name)
            etLastName.setText(lastName)
            etEmail.setText(email)
            passwordView.requestInputFocus()
        }
        mGoogleSignInClient.signOut()
    }


    override val layoutId: Int = R.layout.fragment_register
    override fun getViewModelClass(): KClass<RegisterViewModel> = RegisterViewModel::class
    override val title: CharSequence by lazy { getString(R.string.registration) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}
}