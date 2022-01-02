package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import org.otunjargych.tamtam.adapter.NotesAdapter
import org.otunjargych.tamtam.databinding.FragmentWorkBinding
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.extensions.hideKeyboard
import org.otunjargych.tamtam.extensions.onCompareText
import org.otunjargych.tamtam.extensions.replaceFragment
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.model.State
import org.otunjargych.tamtam.viewmodel.DataViewModel


class WorksFragment : BaseFragment() {

    private lateinit var mAdapter: NotesAdapter
    private val mNotesList: MutableList<Note> = ArrayList()

    private val mViewModel: DataViewModel by activityViewModels()
    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemClickListener: NotesAdapter.OnNoteClickListener


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
        initSearch()
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
        binding.include.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.include.ivClear.setOnClickListener {
            binding.include.etSearch.text.clear()
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
        itemClickListener =
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
                        if (it != null) {
                            mNotesList.addAll(it)
                            binding.progressbar.visibility = ProgressBar.INVISIBLE
                            mAdapter.update(it, itemClickListener)
                            binding.rvListNotes.adapter = mAdapter
                        }
                    }
                }
            }

        })
    }


    private fun initSearch() {
        binding.include.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(binding.include.etSearch)
                showSearchData(binding.include.etSearch.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun showSearchData(searchWord: String) {
        val list = ArrayList<Note>()
        mNotesList.forEach {
//            if (it.text.contains(searchWord, true) || it.text.contentEquals(searchWord, true)) {
//                if (!list.contains(it)) {
//                    list.add(it)
//                }
//            }
            if (onCompareText(it.text, searchWord)){
                if (!list.contains(it)) {
                    list.add(it)
                }
            }
        }
        mAdapter.update(list, itemClickListener)
        binding.rvListNotes.adapter = mAdapter
    }


    override fun onResume() {
        super.onResume()

        if (!binding.include.etSearch.text.isNullOrEmpty()) {
            showSearchData(binding.include.etSearch.text.toString())
        } else {
            mViewModel.loadWorkData()
        }
    }

    override fun onPause() {
        super.onPause()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}