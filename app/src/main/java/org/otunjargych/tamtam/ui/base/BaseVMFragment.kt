package org.otunjargych.tamtam.ui.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.otunjargych.tamtam.ui.main.MainActivity
import org.otunjargych.tamtam.ui.views.LoadingProgressDialog
import kotlin.reflect.KClass

//abstract class BaseVMFragment<VM : BaseViewModel, B : ViewDataBinding> : BaseFragment<B>() {
//
//    protected lateinit var viewModel: VM
//
//    abstract fun getViewModelClass(): KClass<VM>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        initViewModel()
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
//
//    protected open fun initViewModel() {
//        viewModel = getViewModel(null, getViewModelClass())
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.apply { this.lifecycleOwner = viewLifecycleOwner }
//        viewModel.progressData.observe(viewLifecycleOwner) {
//            if (it) {
//                (requireActivity() as MainActivity).showLoadingDialog()
//            } else {
//                (requireActivity() as MainActivity).hideLoadingDialog()
//            }
//        }
//    }
//}