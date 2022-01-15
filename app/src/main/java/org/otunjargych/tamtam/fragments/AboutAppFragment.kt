package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import org.otunjargych.tamtam.databinding.FragmentAboutAppBinding
import org.otunjargych.tamtam.extensions.BaseFragment


class AboutAppFragment : BaseFragment(){

    private lateinit var mToolBar: MaterialToolbar
    private var _binding: FragmentAboutAppBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutAppBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}