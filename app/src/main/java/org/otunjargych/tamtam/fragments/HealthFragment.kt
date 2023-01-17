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
import org.otunjargych.tamtam.databinding.FragmentHealthBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.fragments.dialog_fragments.FiltersFragment
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.viewmodel.NodeViewModel

class HealthFragment : BaseFragment() {

    private var _binding: FragmentHealthBinding? = null
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
        _binding = FragmentHealthBinding.inflate(inflater, container, false)
        initRecyclerView()
        setProgressBarAccordingToLoadState()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarActions()
        onBtnSearchClick()
        binding.include.ivFilter.setOnClickListener {
            showDialog {
                toastMessage(requireContext(), it)
                mSelectedStation = it
                if (hasConnection(requireContext())) {
                    initSearchPagingData(it)
                    binding.include.etSearch.setText(mSelectedStation)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasConnection(requireContext())) {
            if (binding.include.etSearch.text.isNullOrEmpty()) {
                initNodesPagingData()
            } else {
                initSearchPagingData(binding.include.etSearch.text.toString())
            }
        } else {
            binding.progressView.isVisible = true
            toastMessage(requireContext(), getString(R.string.no_connection))
        }

    }

    private fun initNodesPagingData() {
        lifecycleScope.launch {
            mViewModel.loadNodes(NODE_HEALTH).collectLatest {
                mPagingNodesAdapter.submitData(it)

            }
        }
    }

    private fun initSearchPagingData(str: String) {
        lifecycleScope.launch {
            mViewModel.loadSearchNodes(NODE_HEALTH, str).collectLatest {
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
        args.putString("category", getString(R.string.health_category))
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
        binding.include.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.include.ivClear.setOnClickListener {
            binding.include.etSearch.text.clear()
        }
        initTags()

    }

    private fun onBtnSearchClick() {
        binding.include.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            val text = binding.include.etSearch.text
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

    private fun initTags() {
        binding.include.apply {
            Boom(btnTagZero)
            btnTagZero.setOnClickListener {
                binding.include.etSearch.text.clear()
                initNodesPagingData()
            }
            Boom(btnTagFirst)
            btnTagFirst.setOnClickListener {
                binding.include.etSearch.text.clear()
                initSearchPagingData("Поликлиник")
            }
            Boom(btnTagSecond)
            btnTagSecond.setOnClickListener {
                binding.include.etSearch.text.clear()
                initSearchPagingData("Стоматолог")
            }
            Boom(btnTagThird)
            btnTagThird.setOnClickListener {
                binding.include.etSearch.text.clear()
                initSearchPagingData("Красот")
            }
            Boom(btnTagFourth)
            btnTagFourth.setOnClickListener {
                binding.include.etSearch.text.clear()
                initSearchPagingData("Маникюр")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}