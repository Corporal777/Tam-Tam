package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import org.otunjargych.tamtam.adapter.NodesAdapter
import org.otunjargych.tamtam.databinding.FragmentBeautyBinding
import org.otunjargych.tamtam.model.State
import org.otunjargych.tamtam.viewmodel.DataViewModel

class BeautyFragment : Fragment() {

    private var _binding: FragmentBeautyBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: DataViewModel by activityViewModels()

    private lateinit var mAdapter: NodesAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeautyBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarActions()
        initRecyclerView()
        initFirebaseData()
    }

    private fun initToolbarActions() {
        binding.include.customTitle.text = "Здоровье, Красота"
        binding.include.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    private fun initRecyclerView() {
        binding.apply {
            rvListNotes.setHasFixedSize(true)
            rvListNotes.layoutManager = GridLayoutManager(requireContext(), 2)
        }

    }

    private fun initFirebaseData() {
        mAdapter = NodesAdapter()


        mViewModel.medicine.observe(viewLifecycleOwner, { state ->
            when (state) {
                is State.Loading -> {
                    binding.progressbar.visibility = ProgressBar.VISIBLE
                }
                is State.Success -> {
                    state.data.let {
                        if (it != null) {
                            binding.progressbar.visibility = ProgressBar.INVISIBLE
                            binding.rvListNotes.adapter = mAdapter
                        }
                    }
                }
            }

        })
    }


    override fun onResume() {
        super.onResume()
        mViewModel.loadMedicineAndBeautyData()
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}