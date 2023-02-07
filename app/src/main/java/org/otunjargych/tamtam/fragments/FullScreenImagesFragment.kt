package org.otunjargych.tamtam.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import org.otunjargych.tamtam.adapter.FullScreenImageAdapter
import org.otunjargych.tamtam.databinding.FragmentFullImagesBinding
import org.otunjargych.tamtam.util.extensions.BaseFragment
import org.otunjargych.tamtam.util.extensions.OnBottomAppBarStateChangeListener

class FullScreenImagesFragment : org.otunjargych.tamtam.util.extensions.BaseFragment() {

    private lateinit var adapter: FullScreenImageAdapter
    private var listener: OnBottomAppBarStateChangeListener? = null

    private var _binding: FragmentFullImagesBinding? = null
    private val binding get() = _binding!!


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as OnBottomAppBarStateChangeListener
        } catch (e: Exception) {
            throw ClassCastException("Activity not attached!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullImagesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = arguments?.getStringArrayList("images")
        if (list != null) {
            adapter = FullScreenImageAdapter(list)
        }
        binding.vpFullImage.adapter = adapter
        TabLayoutMediator(binding.tabDots, binding.vpFullImage) { tab, position ->
        }.attach()
    }

    override fun onStart() {
        super.onStart()
        listener?.onHide()
    }

    override fun onStop() {
        super.onStop()
        listener?.onShow()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}