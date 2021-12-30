package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.NotesAdapter
import org.otunjargych.tamtam.databinding.FragmentWorkBinding
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.extensions.replaceFragment
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.model.State
import org.otunjargych.tamtam.model.Work
import org.otunjargych.tamtam.viewmodel.DataViewModel

class WorksFragment : BaseFragment() {

    private lateinit var mAdapter: NotesAdapter
    private val mNotesList: MutableList<Work> = ArrayList()

    private val mViewModel: DataViewModel by activityViewModels()
    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarActions()
        initRecyclerView()
        initFirebaseData()

        with(binding) {

//            btnTagFirst.setOnClickListener {
//                val str = "Подработ"
//                initSearchFB(str)
//                mEditTextSearch.setText(str)
//                workList.clear()
//            }
//            btnTagSecond.setOnClickListener {
//                val str = "Халтур"
//                initSearchFB(str)
//                mEditTextSearch.setText(str)
//                workList.clear()
//            }
//            btnTagThird.setOnClickListener {
//                val str = "Постоянн"
//                initSearchFB(str)
//                mEditTextSearch.setText(str)
//                workList.clear()
//            }
//            btnTagFourth.setOnClickListener {
//                val str = "Ищу работу"
//                initSearchFB(str)
//                mEditTextSearch.setText(str)
//                workList.clear()
//            }
        }
    }

    private fun initToolbarActions() {
        binding.workToolbar.customTitle.text = getString(R.string.full_time_work)
        binding.workToolbar.toolbar.setNavigationOnClickListener {
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
        mAdapter = NotesAdapter()
        val itemClickListener: NotesAdapter.OnNoteClickListener =
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

        mViewModel.work.observe(viewLifecycleOwner, { state ->
            when (state) {
                is State.Loading -> {
                    binding.progressbar.visibility = ProgressBar.VISIBLE
                }
                is State.Success -> {
                    state.data.let {
                        if (it != null){
                            mAdapter.update(it, itemClickListener)
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
        mViewModel.loadWorkData()
    }

    override fun onPause() {
        super.onPause()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}