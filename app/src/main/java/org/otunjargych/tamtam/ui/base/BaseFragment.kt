package org.otunjargych.tamtam.ui.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.otunjargych.tamtam.ui.main.MainActivity
import org.otunjargych.tamtam.ui.views.CustomSnackBar
import kotlin.reflect.KClass

abstract class BaseFragment<V : BaseViewModel, Binding : ViewDataBinding>(private val canShowAnim: Boolean = false) :
    Fragment() {

    lateinit var mBinding: Binding

    protected abstract val layoutId: Int
    protected lateinit var viewModel: V
    abstract fun getViewModelClass(): KClass<V>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel(null, getViewModelClass())
        if (canShowAnim) {
            postponeEnterTransition()
            showEnterAnimation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (::mBinding.isInitialized.not()) {
            mBinding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
            mBinding.lifecycleOwner = viewLifecycleOwner
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.progressData.observe(viewLifecycleOwner) {
            if (it) (requireActivity() as MainActivity).showLoadingDialog()
            else (requireActivity() as MainActivity).hideLoadingDialog()
        }
        viewModel.loadingData.observe(viewLifecycleOwner){
            if (it) (requireActivity() as MainActivity).showLoadingView()
            else (requireActivity() as MainActivity).hideLoadingView()
        }
    }

    fun getMainAppBar() = (requireActivity() as MainActivity).getMainAppBar()

    fun showToast(message: String) {
        (requireActivity() as MainActivity).showToast(message)
    }

    fun showSnackBar(message: String, icon: Int) {
        (requireActivity() as MainActivity).showSnackBar(message, icon)
    }

    fun hideKeyboard(view: View) {
        view.clearFocus()
        val inn: InputMethodManager? =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inn?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showEnterAnimation() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = (350).toLong()
        }

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = (400).toLong()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding.unbind()
    }
}