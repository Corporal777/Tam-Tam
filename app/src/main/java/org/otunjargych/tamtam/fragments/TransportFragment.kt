package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.PagingNodesAdapter
import org.otunjargych.tamtam.databinding.FragmentTransportBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.fragments.dialog_fragments.FiltersFragment
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.viewmodel.NodeViewModel


class TransportFragment : BaseFragment() {

    private var _binding: FragmentTransportBinding? = null
    private val binding get() = _binding!!

    private lateinit var mPagingNodesAdapter: PagingNodesAdapter
    private val mViewModel: NodeViewModel by activityViewModels()
    private lateinit var nodeClick: OnNodeClickListener
    private var mSelectedStation = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransportBinding.inflate(inflater, container, false)
        initRecyclerView()
        setProgressBarAccordingToLoadState()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarActions()
        onBtnSearchClick()
        binding.toolbar.ivFilter.setOnClickListener {
            showDialog {
                toastMessage(requireContext(), it)
                mSelectedStation = it
                if (hasConnection(requireContext())) {
                    initSearchPagingData(it)
                    binding.toolbar.etSearch.setText(mSelectedStation)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasConnection(requireContext())) {
            if (binding.toolbar.etSearch.text.isNullOrEmpty()) {
                initNodesPagingData()
            } else {
                initSearchPagingData(binding.toolbar.etSearch.text.toString())
                //initSearchableData(binding.include.etSearch.text.toString())
            }
        } else {
            binding.progressView.isVisible = true
            toastMessage(requireContext(), getString(R.string.no_connection))
        }

    }

    private fun initNodesPagingData() {
        lifecycleScope.launch {
            mViewModel.loadNodes(NODE_TRANSPORT).collectLatest {
                mPagingNodesAdapter.submitData(it)

            }
        }
    }

    private fun initSearchPagingData(str: String) {
        lifecycleScope.launch {
            mViewModel.loadSearchNodes(NODE_TRANSPORT, str).collectLatest {
                mPagingNodesAdapter.submitData(it)
            }
        }
        //binding.listNotes.adapter = mPagingNodesAdapter
    }

    private fun setProgressBarAccordingToLoadState() {
        lifecycleScope.launch {
            mPagingNodesAdapter.loadStateFlow.collectLatest {
                try {
                    binding.progressView.isVisible = it.append is LoadState.Loading
                } catch (e: Exception) {
                    Log.e("LoadingError", e.message.toString())
                }
            }
        }
    }

    private fun showDialog(onData: (String) -> Unit) {
        val args = Bundle()
        args.putString("category", getString(R.string.trans_category))
        val dialog = FiltersFragment(onData)
        dialog.arguments = args
        dialog.show(requireActivity().supportFragmentManager, "dialog")
    }

    private fun initRecyclerView() {
        nodeClick = object : OnNodeClickListener {
            override fun onNodeClick(node: Node, position: Int) {
                if (node != null) {
                    val bundle: Bundle = Bundle()
                    with(bundle) {
                        putParcelable("note", node)
                    }
                    val fragment: DetailFragment = DetailFragment()
                    replaceFragment(fragment)
                    fragment.arguments = bundle
                }
            }

        }
        mPagingNodesAdapter = PagingNodesAdapter(nodeClick)
        binding.rvListNotes.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
        binding.rvListNotes.adapter = mPagingNodesAdapter
    }

    private fun initToolbarActions() {
        binding.toolbar.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.toolbar.ivClear.setOnClickListener {
            binding.toolbar.etSearch.text.clear()
        }
        initTags()

    }

    private fun onBtnSearchClick() {
        binding.toolbar.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            val text = binding.toolbar.etSearch.text
            if (actionId == EditorInfo.IME_ACTION_SEARCH && !text.isNullOrEmpty()) {
                initSearchPagingData(text.toString())
                hideKeyboard(requireView())
                return@OnEditorActionListener true
            } else if (actionId == EditorInfo.IME_ACTION_SEARCH && text.isNullOrEmpty()) {
                hideKeyboard(requireView())
                initNodesPagingData()
            }
            false
        })
    }

    private fun initTags(){
        binding.toolbar.apply {
            Boom(btnTagZero)
            btnTagZero.setOnClickListener {
                binding.toolbar.etSearch.text.clear()
                initNodesPagingData()
            }
            Boom(btnTagFirst)
            btnTagFirst.setOnClickListener {
                initSearchPagingData("Грузоперевоз")
            }
            Boom(btnTagSecond)
            btnTagSecond.setOnClickListener {
                initSearchPagingData("Аренд")
            }
            Boom(btnTagThird)
            btnTagThird.setOnClickListener {
                initSearchPagingData("Выезд")
            }
            Boom(btnTagFourth)
            btnTagFourth.setOnClickListener {
                initSearchPagingData("Такс")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

