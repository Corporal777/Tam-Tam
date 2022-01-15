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
import org.otunjargych.tamtam.adapter.NodesAdapter
import org.otunjargych.tamtam.adapter.PagingNodesAdapter
import org.otunjargych.tamtam.databinding.FragmentWorkBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.fragments.dialog_fragments.FiltersFragment
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.viewmodel.NodeViewModel


class WorksFragment : BaseFragment() {

    private lateinit var mPagingNodesAdapter: PagingNodesAdapter
    private lateinit var mSearchNodesAdapter: NodesAdapter
    private val mViewModel: NodeViewModel by activityViewModels()

    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!

    private val list = ArrayList<Node>()
    private lateinit var nodeClick: OnNodeClickListener
    private var mSelectedStation = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkBinding.inflate(inflater, container, false)
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

    private fun initToolbarActions() {
        binding.include.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.include.ivClear.setOnClickListener {
            binding.include.etSearch.text.clear()
        }

        binding.include.apply {
            Boom(btnTagZero)
            btnTagZero.setOnClickListener {
                binding.include.etSearch.text.clear()
                initNodesPagingData()
            }
            Boom(btnTagFirst)
            btnTagFirst.setOnClickListener {
                initSearchPagingData("Подработ")
            }
            Boom(btnTagSecond)
            btnTagSecond.setOnClickListener {
                initSearchPagingData("Халтур")
            }
            Boom(btnTagThird)
            btnTagThird.setOnClickListener {
                initSearchPagingData("Ищу работу")
            }
            Boom(btnTagFourth)
            btnTagFourth.setOnClickListener {
                initSearchPagingData("Ищу работник")
            }
        }

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
        binding.listNotes.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
        binding.listNotes.adapter = mPagingNodesAdapter
    }

    private fun initNodesPagingData() {
        lifecycleScope.launch {
            mViewModel.loadNodes(NODE_WORKS).collectLatest {
                mPagingNodesAdapter.submitData(it)

            }
        }

    }

    private fun initSearchPagingData(str: String) {
        lifecycleScope.launch {
            mViewModel.loadSearchNodes(NODE_WORKS, str).collectLatest {
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
                    Log.e("Error", e.message.toString())
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
                //initSearchableData(binding.include.etSearch.text.toString())
            }
        } else {
            binding.progressView.isVisible = true
            toastMessage(requireContext(), getString(R.string.no_connection))
        }

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(onData: (String) -> Unit) {
        val args = Bundle()
        args.putString("category", getString(R.string.work_category))
        val dialog = FiltersFragment(onData)
        dialog.arguments = args
        dialog.show(requireActivity().supportFragmentManager, "dialog")
    }

    //   private fun initSearchableData(str: String) {
//        mSearchNodesAdapter = NodesAdapter()
//        mViewModel.loadSearchableNodes(collection)
//        mViewModel.node.observe(viewLifecycleOwner, { state ->
//            when (state) {
//                is State.Loading -> {
//                    binding.progressView.isVisible = true
//                }
//                is State.Success -> {
//                    binding.progressView.isVisible = false
//                    state.data.let { nodeList ->
//                        list.clear()
//                        if (nodeList!!.isNotEmpty()) {
//                            nodeList.forEach { node ->
//                                if (onCompareTitle(node.title, str) || onCompareText(
//                                        node.text,
//                                        str
//                                    ) && !list.contains(node)
//                                ) {
//                                    list.add(node)
//                                }
//                            }
//                            mSearchNodesAdapter.update(list, nodeClick)
//                            binding.listNotes.adapter = mSearchNodesAdapter
//                        }
//                    }
//                }
//            }
//        })
//    }

}