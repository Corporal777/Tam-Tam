package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import org.otunjargych.tamtam.adapter.MyNodesAdapter
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.databinding.FragmentMyNodesBinding
import org.otunjargych.tamtam.extensions.AUTH
import org.otunjargych.tamtam.extensions.BaseSettingsFragment
import org.otunjargych.tamtam.extensions.replaceFragment
import org.otunjargych.tamtam.extensions.toastMessage
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.model.State
import org.otunjargych.tamtam.viewmodel.DataViewModel


class MyNodesFragment : BaseSettingsFragment() {

    private var _binding: FragmentMyNodesBinding? = null
    private val binding get() = _binding!!

    private val mViewModel: DataViewModel by activityViewModels()
    private lateinit var mAdapter: MyNodesAdapter
    private lateinit var nodeClick: MyNodesAdapter.OnNodeClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyNodesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            toolbar.setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            customTitle.text = "Мои объявления"
        }

        initRecyclerView()
        try {
            initData()
        } catch (e: Exception) {
            toastMessage(requireContext(), "Что то пошло не так!")
        }

    }

    private fun initRecyclerView() {
        mAdapter = MyNodesAdapter()
        nodeClick = object : MyNodesAdapter.OnNodeClickListener {
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
                FireBaseHelper.deleteNodeFromFirestore(requireContext(), node.category, node.uuid)
            }

            override fun onNodeEdit(node: Node, position: Int) {
                if (!node.category.isNullOrEmpty()) {
                    val bundle: Bundle = Bundle()
                    with(bundle) {
                        putParcelable("note", node)
                    }
                    val fragment = EditMyNodesFragment()
                    replaceFragment(fragment)
                    fragment.arguments = bundle
                }
            }

        }
        binding.listMyNodes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }


    }

    private fun initData() {
        mViewModel.data.observe(viewLifecycleOwner, {state->
            when(state){
                is State.Loading -> {

                }
                is State.Success -> {
                    state.data.let {
                        if (!it.isNullOrEmpty()){
                            mAdapter.update(it, nodeClick)
                            binding.listMyNodes.adapter = mAdapter
                        }
                    }
                }
                is State.NoItem -> {

                }
            }

        })

    }


    override fun onResume() {
        super.onResume()
        mViewModel.loadMyNodes(AUTH.currentUser?.uid!!)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}