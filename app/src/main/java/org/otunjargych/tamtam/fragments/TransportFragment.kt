package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import org.otunjargych.tamtam.adapter.NotesAdapter
import org.otunjargych.tamtam.databinding.FragmentTransportBinding
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.extensions.replaceFragment
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.model.State
import org.otunjargych.tamtam.viewmodel.DataViewModel


class TransportFragment : BaseFragment() {

    private var _binding: FragmentTransportBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: DataViewModel by activityViewModels()
    private lateinit var mAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransportBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarActions()
        initRecyclerView()
        initFirebaseData()
    }

    private fun initFirebaseData() {
        val itemClick: NotesAdapter.OnNoteClickListener =
            object : NotesAdapter.OnNoteClickListener {
                override fun onNoteClick(note: Note, position: Int) {
                    if (note != null) {
                        val bundle: Bundle = Bundle()
                        with(bundle) {
                            putParcelable("note", note)
                        }
                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }

            }
        mViewModel.transport.observe(viewLifecycleOwner, { state ->
            when (state) {
                is State.Loading -> {
                    binding.progressbar.isVisible = true
                }
                is State.Success -> {
                    state.data.let {
                        if (it != null) {
                            binding.progressbar.isVisible = false
                            mAdapter.update(it, itemClick)
                            binding.rvListNotes.adapter = mAdapter

                        }
                    }
                }
            }
        })

    }

    private fun initRecyclerView() {
        mAdapter = NotesAdapter()
        binding.rvListNotes.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun initToolbarActions() {
        binding.toolbar.customTitle.text = "Транспорт"
        binding.toolbar.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel.loadTransportData()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

