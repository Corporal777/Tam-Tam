package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.databinding.FragmentChangeNameBinding
import org.otunjargych.tamtam.util.extensions.BaseFragment
import org.otunjargych.tamtam.util.extensions.USER_ID
import org.otunjargych.tamtam.util.extensions.toastMessage


class ChangeNameFragment : org.otunjargych.tamtam.util.extensions.BaseFragment() {

    private var _binding: FragmentChangeNameBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangeNameBinding.inflate(inflater, container, false)
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


    private fun changeName(changedName: String, changedLastName: String) {
        FireBaseHelper.changeUserName(USER_ID, changedName, changedLastName)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}