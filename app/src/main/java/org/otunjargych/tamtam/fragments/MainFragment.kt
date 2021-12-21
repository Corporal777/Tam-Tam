package org.otunjargych.tamtam.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.auth.FirebaseAuth
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.NotesAdapter
import org.otunjargych.tamtam.databinding.FragmentMainBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.viewmodel.DataViewModel


class MainFragment : Fragment() {

    private lateinit var adapter: NotesAdapter
    private var mCountAds = 10

    private var listener: OnBottomAppBarStateChangeListener? = null

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: DataViewModel by activityViewModels()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as OnBottomAppBarStateChangeListener
        } catch (e: Exception) {
            throw ClassCastException("Activity not attached!")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
//            duration = resources.getInteger(R.integer.tam_tam_motion_duration_medium).toLong()
//        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.tam_tam_motion_duration_medium).toLong()
        }
//        enterTransition = MaterialFadeThrough().apply {
//            duration = resources.getInteger().toLong()
//        }

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
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
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
        adapter = NotesAdapter()
        val itemClickListener: NotesAdapter.OnItemAdClickListener =
            object : NotesAdapter.OnItemAdClickListener {
                override fun onAdClick(note: Note, position: Int) {
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

        mViewModel.data.observe(viewLifecycleOwner, {
            adapter.update(it, itemClickListener)
            binding.progressbar.visibility = ProgressBar.INVISIBLE
            binding.recyclerView.adapter = adapter
        })
    }

    private fun stateShowBottomAppBar() {
        listener?.onShow()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.loadWorkDataForMain(mCountAds)
        stateShowBottomAppBar()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

