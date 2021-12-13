package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.WorksAdapter
import org.otunjargych.tamtam.databinding.FragmentWorkBinding
import org.otunjargych.tamtam.extensions.AppValueEventListener
import org.otunjargych.tamtam.extensions.BaseFragment
import org.otunjargych.tamtam.extensions.NODE_WORKS
import org.otunjargych.tamtam.extensions.replaceFragment
import org.otunjargych.tamtam.model.Work

class WorksFragment : BaseFragment() {

    private lateinit var adapter: WorksAdapter
    private val workList: MutableList<Work> = ArrayList()
    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            workToolbar.apply {
                toolbar.setNavigationOnClickListener {
                    requireActivity().supportFragmentManager.popBackStack()
                }
                toolbar.title = getString(R.string.full_time_work)
            }
        }
        initRecyclerView()

        with(binding) {

//            btnTagFirst.setOnClickListener {
//                val str = "Подработ"
//                initSearchFB(str)
//                mEditTextSearch.setText(str)
//                workList.clear()
//            }
//            btnTagSecond.setOnClickListener {
//                val str = "Халтур"
//                initSearchFB(str)
//                mEditTextSearch.setText(str)
//                workList.clear()
//            }
//            btnTagThird.setOnClickListener {
//                val str = "Постоянн"
//                initSearchFB(str)
//                mEditTextSearch.setText(str)
//                workList.clear()
//            }
//            btnTagFourth.setOnClickListener {
//                val str = "Ищу работу"
//                initSearchFB(str)
//                mEditTextSearch.setText(str)
//                workList.clear()
//            }
        }
    }

    private fun initRecyclerView() {
        binding.apply {
            rvWork.setHasFixedSize(true)
            rvWork.layoutManager = GridLayoutManager(requireContext(), 2)
        }

    }

    private fun initFireBaseData() {
        adapter = WorksAdapter()
        val stateAdListener: WorksAdapter.OnItemAdClickListener =
            object : WorksAdapter.OnItemAdClickListener {
                override fun onAdClick(work: Work, position: Int) {
                    if (work != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", work.imageURL)
                        bundle.putSerializable("text", work.text)
                        bundle.putSerializable("category", work.category)
                        bundle.putSerializable("date", work.timeStamp.toString())
                        bundle.putSerializable("title", work.title)
                        bundle.putSerializable("station", work.station)
                        bundle.putSerializable("salary", work.salary)
                        bundle.putSerializable("phone", work.phone_number)
                        bundle.putSerializable("likes", work.likes)
                        bundle.putSerializable("viewings", work.viewings)
                        bundle.putSerializable("id", work.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }

        mRefAds = FirebaseDatabase.getInstance().reference.child(NODE_WORKS)
        mRefListener = AppValueEventListener {
            for (dataSnapshot: DataSnapshot in it.children) {
                var work: Work = dataSnapshot.getValue(Work::class.java)!!
                if (!workList.contains(work) && work != null) {
                    workList.add(0, work)
                }
                adapter.update(workList, stateAdListener)
                binding.progressbar.visibility = ProgressBar.INVISIBLE
            }
            binding.rvWork.adapter = adapter
        }
        mRefAds.addListenerForSingleValueEvent(mRefListener)
        mapListeners[mRefAds] = mRefListener

    }


    override fun onResume() {
        super.onResume()
        initFireBaseData()
    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}