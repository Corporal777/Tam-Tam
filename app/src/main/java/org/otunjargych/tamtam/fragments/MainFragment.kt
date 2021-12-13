package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.WorksAdapter
import org.otunjargych.tamtam.databinding.FragmentMainBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.model.Work
import org.otunjargych.tamtam.viewmodel.DataViewModel


class MainFragment : Fragment() {

    private lateinit var adapter: WorksAdapter
    private var mCountAds = 10

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: DataViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_medium_1).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.material_motion_duration_medium_1).toLong()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        USER = FirebaseAuth.getInstance()
        AUTH = FirebaseAuth.getInstance()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!hasConnection(requireContext())) {
            errorConnection(view)
        }
        initRecyclerView()
        initFirebaseData()
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
                replaceFragment(FlatsFragment())
            }
            category.caseStudy.setOnClickListener {
                replaceFragment(ServicesFragment())
            }
            category.caseMedicine.setOnClickListener {
                replaceFragment(BeautyFragment())
            }
        }
    }


    private fun initRecyclerView() {
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        }

    }


    private fun initFirebaseData() {
        adapter = WorksAdapter()
        val itemClickListener: WorksAdapter.OnItemAdClickListener =
            object : WorksAdapter.OnItemAdClickListener {
                override fun onAdClick(work: Work, position: Int) {
                    if (work != null) {
                        val bundle: Bundle = Bundle()
                        with(bundle) {
                            putSerializable("image_first", work.imageURL)
                            putSerializable("text", work.text)
                            putSerializable("category", work.category)
                            putSerializable("date", work.timeStamp.toString())
                            putSerializable("title", work.title)
                            putSerializable("station", work.station)
                            putSerializable("salary", work.salary)
                            putSerializable("phone", work.phone_number)
                            putSerializable("likes", work.likes)
                            putSerializable("viewings", work.viewings)
                            putSerializable("id", work.id)
                        }
                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }

        mViewModel.data.observe(viewLifecycleOwner, {
            adapter.update(it, itemClickListener)
            binding.progressbar.visibility = ProgressBar.INVISIBLE
            binding.recyclerView.adapter = adapter
        })
    }

    override fun onResume() {
        super.onResume()
        mViewModel.loadWorkDataForMain(mCountAds)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

