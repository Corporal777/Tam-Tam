package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.PagingNodesAdapter
import org.otunjargych.tamtam.databinding.FragmentBuySellBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.fragments.dialog_fragments.FiltersFragment
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.viewmodel.NodeViewModel

class BuySellFragment : BaseFragment() {

    private var _binding: FragmentBuySellBinding? = null
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
        _binding = FragmentBuySellBinding.inflate(inflater, container, false)
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
            }
        } else {
            binding.progressView.isVisible = true
            toastMessage(requireContext(), getString(R.string.no_connection))
        }

    }

    private fun initNodesPagingData() {
        lifecycleScope.launch {
            mViewModel.loadNodes(NODE_BUY_SELL).collectLatest {
                mPagingNodesAdapter.submitData(it)

            }
        }
    }

    private fun initSearchPagingData(str: String) {
        lifecycleScope.launch {
            mViewModel.loadSearchNodes(NODE_BUY_SELL, str).collectLatest {
                mPagingNodesAdapter.submitData(it)
            }
        }
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
        args.putString("category", getString(R.string.buy_sell_category))
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
                binding.toolbar.etSearch.text.clear()
                initSearchPagingData("Телефон")
            }
            Boom(btnTagSecond)
            btnTagSecond.setOnClickListener {
                binding.toolbar.etSearch.text.clear()
                initSearchPagingData("Одежд")
            }
            Boom(btnTagThird)
            btnTagThird.setOnClickListener {
                binding.toolbar.etSearch.text.clear()
                initSearchPagingData("Авто")
            }
            Boom(btnTagFourth)
            btnTagFourth.setOnClickListener {
                binding.toolbar.etSearch.text.clear()
                initSearchPagingData("Сатылат")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}