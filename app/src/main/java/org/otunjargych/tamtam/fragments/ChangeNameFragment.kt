package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.firebase.database.DataSnapshot
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.databinding.FragmentChangeNameBinding
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.extensions.USER_ID
import org.otunjargych.tamtam.extensions.toastMessage
import org.otunjargych.tamtam.model.State
import org.otunjargych.tamtam.viewmodel.UserViewModel


class ChangeNameFragment : BaseFragment() {

    private var _binding: FragmentChangeNameBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: UserViewModel by activityViewModels()
    private lateinit var mSnapshot: DataSnapshot

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        initData()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (binding.etLastName.text.isNullOrEmpty()) {
            binding.etLastName.requestFocus()

        }
        binding.ivDone.setOnClickListener {
            if (binding.etName.text.isNullOrEmpty() || binding.etLastName.text.isNullOrEmpty()) {
                toastMessage(requireContext(), "Поля не могут быть пусты!")
            } else {
                changeName(binding.etName.text.toString(), binding.etLastName.text.toString())
                toastMessage(requireContext(), "Имя изменено!")
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        binding.customTitle.text = "Изменить имя"
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun initData() {
        mViewModel.user.observe(viewLifecycleOwner, { state ->
            when (state) {
                is State.Loading -> {

                }
                is State.Success -> {
                    state.data.let {
                        if (it != null) {
                            mSnapshot = it
                        }
                    }
                }
            }
        })
    }

    private fun changeName(changedName: String, changedLastName: String) {
        if (mSnapshot != null){
            FireBaseHelper.changeUserName(changedName, changedLastName, mSnapshot)
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.loadUserData(USER_ID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}