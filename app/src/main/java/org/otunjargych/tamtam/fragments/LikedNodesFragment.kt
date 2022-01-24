package org.otunjargych.tamtam.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.otunjargych.tamtam.adapter.LikedNodesAdapter
import org.otunjargych.tamtam.data.room.DatabaseBuilder
import org.otunjargych.tamtam.data.room.DatabaseHelperImpl
import org.otunjargych.tamtam.databinding.FragmentLikedNodesBinding
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.extensions.OnBottomAppBarItemsEnabledListener
import org.otunjargych.tamtam.extensions.replaceFragment
import org.otunjargych.tamtam.extensions.toastMessage
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.model.State
import org.otunjargych.tamtam.viewmodel.LikedNodesViewModel

class LikedNodesFragment : BaseFragment() {

    private lateinit var mAdapter: LikedNodesAdapter
    private var nodeList = ArrayList<Node>()
    private val mViewModel: LikedNodesViewModel by activityViewModels()
    private lateinit var stateAdListener: LikedNodesAdapter.OnNodeClickListener
    private var _binding: FragmentLikedNodesBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelperImpl
    private var listener: OnBottomAppBarItemsEnabledListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as OnBottomAppBarItemsEnabledListener
        } catch (e: Exception) {
            throw ClassCastException("Activity not attached!")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLikedNodesBinding.inflate(inflater, container, false)

        try {
            dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(requireContext()))
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.customTitle.text = "Избранное"
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        initRecyclerView()
        initDataFromLocalDatabase()
    }


    private fun initRecyclerView() {
        mAdapter = LikedNodesAdapter()
        binding.rvLikedNodes.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }


        stateAdListener = object : LikedNodesAdapter.OnNodeClickListener {
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

            override fun onNodeDelete(node: Node, position: Int) {
                mViewModel.deleteNode(node)
                toastMessage(requireContext(), "Удалено из избранных")
            }
        }
    }

    private fun initDataFromLocalDatabase() {
        mViewModel.liked.observe(viewLifecycleOwner, { state ->
            when (state) {
                is State.Loading -> {
                    binding.progressView.isVisible = true
                }
                is State.Success -> {
                    state.data.let {
                        if (it != null) {
                            binding.progressView.isVisible = false
                            mAdapter.update(it, stateAdListener)
                            binding.rvLikedNodes.adapter = mAdapter
                        }
                    }
                }
                is State.Error -> {
                    binding.progressView.isVisible = true
                    toastMessage(requireContext(), "Что то пошло не так")
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mViewModel.fetchLikedNodes(dbHelper)
    }

    override fun onStart() {
        super.onStart()
        listener?.disabledLikedItem()
    }

    override fun onStop() {
        super.onStop()
        listener?.enabledLikedItem()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}