package org.otunjargych.tamtam.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.google.android.material.transition.MaterialSharedAxis
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.NodesAdapter
import org.otunjargych.tamtam.adapter.VipNodesAdapter
import org.otunjargych.tamtam.databinding.FragmentMainBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.model.State
import org.otunjargych.tamtam.viewmodel.NodeViewModel


class MainFragment : Fragment() {

    private lateinit var mAdapter: NodesAdapter
    private lateinit var mAdapterVip: VipNodesAdapter
    val noteList: MutableList<Note> = ArrayList()
    private var listener: OnBottomAppBarStateChangeListener? = null
    private lateinit var nodeClick: OnNodeClickListener

    private var itemListener: OnBottomAppBarItemsEnabledListener? = null

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: NodeViewModel by activityViewModels()

    private lateinit var mLayoutManager: GridLayoutManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as OnBottomAppBarStateChangeListener
        } catch (e: Exception) {
            throw ClassCastException("Activity not attached!")
        }
        itemListener = try {
            context as OnBottomAppBarItemsEnabledListener
        }catch (e : Exception){
            throw ClassCastException("Activity not attached!")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.tam_tam_motion_duration_medium).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        boom()
        with(binding) {
            category.caseWork.setOnClickListener {
                replaceFragment(WorksFragment())
            }
            category.caseTransport.setOnClickListener {
                replaceFragment(TransportFragment())
            }
            category.caseBuy.setOnClickListener {
                replaceFragment(BuySellFragment())
            }
            category.caseFlats.setOnClickListener {
                replaceFragment(HouseFragment())
            }
            category.caseStudy.setOnClickListener {
                replaceFragment(ServicesFragment())
            }
            category.caseMedicine.setOnClickListener {
                replaceFragment(HealthFragment())
            }
        }
        initRecyclerView()
        initFirebaseData()
    }

    private fun initRecyclerView() {
        mAdapterVip = VipNodesAdapter()
        mAdapter = NodesAdapter()
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
        binding.apply {
            recyclerView.setHasFixedSize(true)
            mLayoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.layoutManager = mLayoutManager

            recyclerViewVip.setHasFixedSize(true)
            recyclerViewVip.layoutManager = LinearLayoutManager(requireContext(),
                HORIZONTAL, false
            )
        }
    }

    private fun initFirebaseData() {
        mViewModel.node.observe(viewLifecycleOwner, { state ->
            when (state) {
                is State.Loading -> {
                    binding.progressView.isVisible = true
                }
                is State.Success -> {
                    state.data.let {
                        if (it != null) {
                            binding.progressView.isVisible = false
                            mAdapter.update(it, nodeClick)
                            binding.recyclerView.adapter = mAdapter
                        }
                    }
                }
                is State.Error -> {
                    binding.progressView.isVisible = true
                    toastMessage(requireContext(), "Что то пошло не так")
                }
            }
        })
        mViewModel.vip.observe(viewLifecycleOwner, {
            if(it != null){
                mAdapterVip.update(it, nodeClick)
                binding.recyclerViewVip.adapter = mAdapterVip
            }
        })
    }

    private fun showBottomAppBar() {
        listener?.onShow()
    }

    override fun onResume() {
        super.onResume()
        if (hasConnection(requireContext())) {
            mViewModel.loadActualNodes(NODE_WORKS)
            mViewModel.loadVipNodes()
        } else toastMessage(requireContext(), getString(R.string.no_connection))
        showBottomAppBar()
    }


    private fun boom() {
        binding.apply {
            Boom(category.caseBuy)
            Boom(category.caseStudy)
            Boom(category.caseMedicine)
            Boom(category.caseFlats)
            Boom(category.caseTransport)
            Boom(category.caseWork)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        itemListener?.disabledHomeItem()
    }

    override fun onStop() {
        super.onStop()
        itemListener?.enabledHomeItem()
    }

}






