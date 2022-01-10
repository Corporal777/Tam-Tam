package org.otunjargych.tamtam.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.google.firebase.database.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.NodesAdapter
import org.otunjargych.tamtam.databinding.FragmentMainBinding
import org.otunjargych.tamtam.extensions.OnBottomAppBarStateChangeListener
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.extensions.replaceFragment
import org.otunjargych.tamtam.model.Note
import org.otunjargych.tamtam.viewmodel.DataViewModel


class MainFragment : Fragment() {

    private lateinit var mAdapter: NodesAdapter
    private var mCountAds = 5
    val noteList: MutableList<Note> = ArrayList()
    private var listener: OnBottomAppBarStateChangeListener? = null

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val mViewModel: DataViewModel by activityViewModels()

    private var mIsScrolling = false

    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: ChildEventListener
    private lateinit var mLayoutManager: GridLayoutManager

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
        binding.apply {
            //recyclerView.setHasFixedSize(true)
            mLayoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.layoutManager = mLayoutManager
            recyclerView.isNestedScrollingEnabled = true
        }
        //initFirebaseData()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        postponeEnterTransition()
//        view.doOnPreDraw { startPostponedEnterTransition() }

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


    private fun showBottomAppBar() {
        listener?.onShow()
    }

    override fun onResume() {
        super.onResume()
        //mViewModel.loadLastNoteData(mCountAds)


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


}






