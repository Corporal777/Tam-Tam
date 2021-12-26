package org.otunjargych.tamtam.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.TransportAdapter
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment
import org.otunjargych.tamtam.model.Transportation
import java.util.*
import kotlin.collections.ArrayList


class TransportFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCardView: CardView
    private lateinit var dialog: MyDialogFragment
    private var selected_station: String? = null
    val list: MutableList<Transportation> = ArrayList()
    private lateinit var mImageViewBack: ImageView
    private lateinit var mImageViewAdd: ImageView
    private lateinit var mEditTextSearch: EditText
    private lateinit var mAdapter: TransportAdapter
    val adsList: MutableList<Transportation> = ArrayList()
    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    private lateinit var mProgressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_transport, container, false)
        mRecyclerView = view.findViewById(R.id.recycler_view_transport)
        mCardView = view.findViewById(R.id.card_metro)
        val mCardViewFirst: CardView = view.findViewById(R.id.card_work_first)
        val mCardViewSecond: CardView = view.findViewById(R.id.card_work_second)
        val mCardViewThird: CardView = view.findViewById(R.id.card_work_third)
        mImageViewAdd = view.findViewById(R.id.iv_add)
        val mImageViewClear: ImageView = view.findViewById(R.id.iv_clear)
        mEditTextSearch = view.findViewById(R.id.et_search_transport)
        mProgressBar = view.findViewById(R.id.progressbar)
        mRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        mProgressBar.visibility = ProgressBar.VISIBLE

        mImageViewClear.setOnClickListener {
            selected_station = null
            mEditTextSearch.text.clear()
            adsList.clear()
            initFB()
        }

        mImageViewAdd.setOnClickListener {
            initCurrentUser()
        }
        mCardView.setOnClickListener {

            if (!hasConnection(context!!)) {
                errorToast("Нет интернета!", activity!!)
            } else {
                val manager: FragmentManager? = fragmentManager
                dialog = MyDialogFragment()
                dialog.show(manager!!, "metro3")
            }
        }
        mCardViewFirst.setOnClickListener {
            val str = "Грузоперевоз"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            adsList.clear()
        }
        mCardViewSecond.setOnClickListener {
            val str = "Авто"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            adsList.clear()
        }
        mCardViewThird.setOnClickListener {
            val str = "Выезд, Заезд"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            adsList.clear()
        }


        return view
    }

    private fun initCurrentUser() {
        AUTH = FirebaseAuth.getInstance()
        if (hasConnection(context!!)){
            if (AUTH.currentUser != null) {
                replaceFragment(NewAdFragment())
            }else {
                errorToast("Войдите в аккаунт!", activity!!)
            }
        }
        else {
            Snackbar.make(view!!, "Нет интернет соединения!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
        }
    }

    override fun onResume() {
        super.onResume()
        initSearch()
        if (!mEditTextSearch.text.isEmpty() || selected_station != null) {
            initSearchFB(mEditTextSearch.text.toString())
            adsList.clear()
            initSearch()
        } else {
            initFB()
        }

    }

    private fun initFB() {
        mAdapter = TransportAdapter()
        val stateAdListener: TransportAdapter.OnAdClickListener =
            object : TransportAdapter.OnAdClickListener {
                override fun onAdClick(transport: Transportation, position: Int) {
                    if (transport != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", transport.imageURL)
                        bundle.putSerializable("text", transport.text)
                        bundle.putSerializable("category", transport.category)
                        bundle.putSerializable("date", transport.timeStamp.toString())
                        bundle.putSerializable("title", transport.title)
                        bundle.putSerializable("station", transport.station)
                        bundle.putSerializable("salary", transport.salary)
                        bundle.putSerializable("phone", transport.phone_number)
                        bundle.putSerializable("likes", transport.likes)
                        bundle.putSerializable("viewings", transport.viewings)
                        bundle.putSerializable("id", transport.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }
        GlobalScope.launch {
            mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_TRANSPORT)
            mRefListener = AppValueEventListener {
                adsList.clear()
                for (dataSnapshot: DataSnapshot in it.children) {
                    var transport: Transportation =
                        dataSnapshot.getValue(Transportation::class.java)!!

                    if (!adsList.contains(transport)) {
                        adsList.add(0, transport)
                    }
                    mAdapter.update(adsList, stateAdListener)
                    mRecyclerView.adapter = mAdapter
                    mProgressBar.visibility = View.INVISIBLE
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
            mapListeners[mRefAds] = mRefListener
        }
    }

    private fun initSearchFB(search_word: String) {
        mAdapter = TransportAdapter()
        val stateAdListener: TransportAdapter.OnAdClickListener =
            object : TransportAdapter.OnAdClickListener {
                override fun onAdClick(transp: Transportation, position: Int) {
                    if (transp != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", transp.imageURL)
                        bundle.putSerializable("text", transp.text)
                        bundle.putSerializable("category", transp.category)
                        bundle.putSerializable("date", transp.timeStamp.toString())
                        bundle.putSerializable("title", transp.title)
                        bundle.putSerializable("station", transp.station)
                        bundle.putSerializable("salary", transp.salary)
                        bundle.putSerializable("likes", transp.likes)
                        bundle.putSerializable("viewings", transp.viewings)
                        bundle.putSerializable("id", transp.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }
        mRefAds = FirebaseDatabase.getInstance().getReference(NODE_TRANSPORT)
        mRefListener = AppValueEventListener {
            for (dataSnapshot: DataSnapshot in it.children) {
                var transp: Transportation = dataSnapshot.getValue(Transportation::class.java)!!
                if (selected_station == null && transp.text.contains(search_word, true)) {
                    adsList.add(0, transp)
                    mAdapter.update(adsList, stateAdListener)
                }
                if (selected_station == transp.station && transp.text.contains(search_word, true)) {
                    adsList.add(0, transp)
                    mAdapter.update(adsList, stateAdListener)
                }
                if (selected_station == transp.station && search_word == selected_station) {
                    adsList.add(0, transp)
                    mAdapter.update(adsList, stateAdListener)
                }
                mRecyclerView.adapter = mAdapter
                mProgressBar.visibility = ProgressBar.INVISIBLE
            }
        }
        mRefAds.addListenerForSingleValueEvent(mRefListener)
        mapListeners[mRefAds] = mRefListener
    }

    private fun performSearch() {
        mEditTextSearch.clearFocus()
        val inn: InputMethodManager? =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inn?.hideSoftInputFromWindow(mEditTextSearch.windowToken, 0)

    }

    private fun initSearch() {
        mEditTextSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                initSearchFB(mEditTextSearch.text.toString())
                performSearch()
                adsList.clear()
                Toast.makeText(activity, "Successfully", Toast.LENGTH_LONG).show()
                return@OnEditorActionListener true
            }
//            if (actionId == EditorInfo.IME_ACTION_SEARCH && mEditTextSearch.text.isEmpty()) {
//                initFB()
//                return@OnEditorActionListener true
//            }
            false
        })
    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }


    fun setSelectedStation(selectedItem: String?) {
        selected_station = selectedItem!!
        mEditTextSearch.setText(selected_station)
    }
}

