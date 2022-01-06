package org.otunjargych.tamtam.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.adapter.NotesAdapter
import org.otunjargych.tamtam.adapter.PagingAdapter
import org.otunjargych.tamtam.databinding.FragmentWorkBinding
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.viewmodel.NoteViewModel


class WorksFragment : BaseFragment() {

    private lateinit var mAdapter: PagingAdapter

    private val mViewModel: NoteViewModel by viewModels()
    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!
    private val compositeDisposable = CompositeDisposable()

    private lateinit var mDatabase: FirebaseDatabase
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
        initData()

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

        binding.rvListNotes.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
        mAdapter = PagingAdapter()
        binding.rvListNotes.adapter = mAdapter

    }

    @SuppressLint("CheckResult")
    private fun initData() {
        lifecycleScope.launch {
            mViewModel.load().collectLatest {
                mAdapter.submitData(it)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}