package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.LikedAdsAdapter
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.model.LikedAds

class LikedAdsFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mAdapter: LikedAdsAdapter
    val adOneList: MutableList<LikedAds> = ArrayList()
    private lateinit var mLinearLayout: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_liked_ads, container, false)
        mRecyclerView = view.findViewById(R.id.liked_ads_list)
        mProgressBar = view.findViewById(R.id.progressbar)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mLinearLayout = view.findViewById(R.id.ln_empty)

        return view

    }

    override fun onResume() {
        super.onResume()
        init()
    }


    private fun init() {
        mAdapter = LikedAdsAdapter()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val stateAdListener: LikedAdsAdapter.OnAdClickListener =
            object : LikedAdsAdapter.OnAdClickListener {
                override fun onAdClick(likedAds: LikedAds, position: Int) {
                    if (likedAds != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("text", likedAds.text)
                        bundle.putSerializable("category", likedAds.category)
                        bundle.putSerializable("date", likedAds.timeStamp.toString())
                        bundle.putSerializable("title", likedAds.title)
                        bundle.putSerializable("station", likedAds.station)
                        bundle.putSerializable("salary", likedAds.salary)
                        bundle.putSerializable("phone", likedAds.phone_number)
                        bundle.putSerializable("likes", likedAds.likes)
                        bundle.putSerializable("viewings", likedAds.viewings)
                        bundle.putSerializable("id", likedAds.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }

                override fun onAdDelete(likedAds: LikedAds, position: Int) {
                    if (hasConnection(context!!)){
                        successToast("Удалено из избранного", activity!!)
                        adOneList.remove(likedAds)
                        mAdapter.notifyItemRemoved(position)
                        mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_USERS)
                            .child(uid.toString()).child(NODE_LIKED_ADS)
                        mRefListener = AppValueEventListener {
                            for (dataSnapshot: DataSnapshot in it.children) {
                                var adOne: LikedAds = dataSnapshot.getValue(LikedAds::class.java)!!
                                if (adOne.text == likedAds.text) {
                                    dataSnapshot.ref.removeValue()
                                }
                            }
                        }
                        mRefAds.addListenerForSingleValueEvent(mRefListener)
                        mapListeners[mRefAds] = mRefListener
                    }else{
                        Snackbar.make(view!!, "Нет интернет соединения!", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
                    }

                }
            }


        GlobalScope.launch {
            adOneList.clear()
            mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_USERS)
                .child(uid.toString()).child(NODE_LIKED_ADS)
            mRefListener = AppValueEventListener {

                if (!it.exists()) {
                    mProgressBar.visibility = ProgressBar.INVISIBLE
                    mLinearLayout.visibility = View.VISIBLE

                } else {
                    mLinearLayout.visibility = View.INVISIBLE
                    for (dataSnapshot: DataSnapshot in it.children) {
                        var adOne: LikedAds = dataSnapshot.getValue(LikedAds::class.java)!!

                        if (!adOneList.contains(adOne) && adOne != null) {
                            adOneList.add(adOne)
                        }
                        mProgressBar.visibility = ProgressBar.INVISIBLE
                    }
                    mAdapter.update(adOneList, stateAdListener)
                    mRecyclerView.adapter = mAdapter
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
            mapListeners[mRefAds] = mRefListener
        }
    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }
}