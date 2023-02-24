package org.otunjargych.tamtam.ui.confirmCode

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.BottomSheetChangeLoginBinding
import org.otunjargych.tamtam.databinding.BottomSheetConfirmCodeBinding
import org.otunjargych.tamtam.ui.base.BaseBottomSheetFragment
import org.otunjargych.tamtam.ui.profileSettings.changeLogin.ChangeLoginFragment
import org.otunjargych.tamtam.ui.profileSettings.changeLogin.ChangeLoginViewModel
import org.otunjargych.tamtam.ui.views.CustomColorSpan
import org.otunjargych.tamtam.ui.views.CustomTypefaceSpan
import org.otunjargych.tamtam.util.onTextChanged
import org.otunjargych.tamtam.util.timerFormatter
import kotlin.reflect.KClass

class ConfirmCodeFragment(val type: ConfirmType, val login: String) :
    BaseBottomSheetFragment<ConfirmCodeViewModel, BottomSheetConfirmCodeBinding>() {

    private val timerMessage by lazy { getString(R.string.again_send_possible_after_text) }

    private var onConfirmCode: () -> Unit = {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setLoginAndType(login, type)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            ivClose.setOnClickListener {
                dismiss()
            }

            customTitle.text = getSpannedTitle()
            tvConfirmationInfo.text = getSpannedInformationText(login)

            etCode.apply {
                onTextChanged { viewModel.performChangeCode(it.toString()) }
            }
            btnSendAgain.setOnClickListener {
                viewModel.sendCodeAgain()
            }
            btnConfirm.setOnClickListener {
                hideKeyboard(it)
                viewModel.confirmCode()
            }
        }
        observeCodeSuccess()
        observeTimer()
        observeButtonsStates()
    }


    private fun observeCodeSuccess() {
        viewModel.codeSuccess.observe(viewLifecycleOwner) {
            when (it) {
                null -> mBinding.tilCode.error = null
                false -> mBinding.tilCode.error = getString(R.string.code_incorrect)
                true -> {
                    onConfirmCode.invoke()
                    dismiss()
                }
            }
        }
    }

    private fun observeButtonsStates() {
        viewModel.enableBtnConfirm.observe(viewLifecycleOwner) {
            mBinding.btnConfirm.isEnabled = it
        }
        viewModel.enableBtnSendAgain.observe(viewLifecycleOwner) {
            mBinding.btnSendAgain.isEnabled = it
        }
    }

    private fun observeTimer() {
        viewModel.timer.observe(viewLifecycleOwner) {
            val quantity = timerFormatter(it, requireContext())
            mBinding.tvTimeInfo.apply {
                isVisible = it > 0
                text = String.format(timerMessage, quantity)
            }
        }
    }

    fun setCodeCallback(block: () -> Unit): ConfirmCodeFragment {
        onConfirmCode = block
        return this
    }

    private fun getSpannedTitle(): String {
        return if (type == ConfirmType.EMAIL) getString(R.string.confirm_login, "почты")
        else getString(R.string.confirm_login, "номера")
    }

    private fun getSpannedInformationText(login: String): CharSequence {
        return if (type == ConfirmType.EMAIL) {
            SpannableString(getString(R.string.email_confirmation_info, login)).apply {
                setSpan(CustomColorSpan(requireContext(), R.color.app_main_color), 14, 14 + login.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(CustomTypefaceSpan(requireContext(), "noto_sans_semibold.ttf"), 14, 14 + login.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        } else {
            SpannableString(getString(R.string.phone_confirmation_info, login))
                .apply {
                    setSpan(CustomColorSpan(requireContext(), R.color.app_main_color), 13, 13 + login.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(CustomTypefaceSpan(requireContext(), "noto_sans_semibold.ttf"), 13, 13 + login.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
        }
    }

    override val layoutId: Int = R.layout.bottom_sheet_confirm_code
    override fun getViewModelClass(): KClass<ConfirmCodeViewModel> = ConfirmCodeViewModel::class

    enum class ConfirmType {
        PHONE, EMAIL
    }
}