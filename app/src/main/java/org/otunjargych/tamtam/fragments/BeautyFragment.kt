package org.otunjargych.tamtam.fragments

import android.app.Activity
import android.os.Bundle
import android.os.Handler
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.R

import org.otunjargych.tamtam.adapter.BeautiesAdapter
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment
import org.otunjargych.tamtam.model.Beauty
import java.util.*

class BeautyFragment : Fragment() {


    private lateinit var dialog: MyDialogFragment
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mImageViewBack: ImageView
    private lateinit var mImageViewAdd: ImageView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mImageViewClear: ImageView
    private lateinit var mEditTextSearch: EditText
    private lateinit var mCardViewFirst: CardView
    private lateinit var mCardViewSecond: CardView
    private lateinit var mCardViewThird: CardView
    private lateinit var mCardViewFourth: CardView
    private lateinit var mAdapter: BeautiesAdapter
    private lateinit var mCardView: CardView
    val beautyList: MutableList<Beauty> = ArrayList()
    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    private var selected_station: String? = null
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_beauty, container, false)
        mRecyclerView = view.findViewById(R.id.rv_beauty_list)
        mImageViewBack = view.findViewById(R.id.iv_back)
        mImageViewAdd = view.findViewById(R.id.iv_add)
        mImageViewClear = view.findViewById(R.id.iv_clear)
        mProgressBar = view.findViewById(R.id.progressbar)
        mCardView = view.findViewById(R.id.card_metro)
        mCardViewFirst = view.findViewById(R.id.card_beauty_first)
        mCardViewSecond = view.findViewById(R.id.card_beauty_second)
        mCardViewThird = view.findViewById(R.id.card_beauty_third)
        mCardViewFourth = view.findViewById(R.id.card_beauty_fourth)
        mEditTextSearch = view.findViewById(R.id.et_search_beauty)
        mRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        mProgressBar.visibility = ProgressBar.VISIBLE

        mCardView.setOnClickListener {
            if (!hasConnection(context!!)) {
                errorToast("Нет интернета!", activity!!)
            } else {
                val manager: FragmentManager? = fragmentManager
                dialog = MyDialogFragment()
                dialog.show(manager!!, "metro2")
            }
        }
        mImageViewBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        mImageViewAdd.setOnClickListener {
            initCurrentUser()
        }
        mImageViewClear.setOnClickListener {
            selected_station = null
            mEditTextSearch.text.clear()
            beautyList.clear()
            initFB()
        }
        mCardViewFirst.setOnClickListener {
            val str = "Поликлиник"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            beautyList.clear()
        }
        mCardViewSecond.setOnClickListener {
            val str = "Стоматолог"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            beautyList.clear()
        }
        mCardViewThird.setOnClickListener {
            val str = "Салон красоты"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            beautyList.clear()
        }
        mCardViewFourth.setOnClickListener {
            val str = "Маникюр"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            beautyList.clear()
        }

        return view
    }

    private fun initCurrentUser() {
        AUTH = FirebaseAuth.getInstance()
        if (hasConnection(context!!)) {
            if (AUTH.currentUser != null) {
                replaceFragment(AdFragment())
            } else {
                errorToast("Войдите в аккаунт!", activity!!)
            }
        } else {
            Snackbar.make(view!!, "Нет интернет соединения!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
        }
    }


    override fun onResume() {
        super.onResume()
        if (!mEditTextSearch.text.isEmpty() || selected_station != null) {
            initSearchFB(mEditTextSearch.text.toString())
            beautyList.clear()
            initSearch()
        } else {
            initFB()
        }
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
        mHandler = Handler()
        mRunnable = Runnable {
            dialog.dismiss()
        }
        mHandler!!.postDelayed(mRunnable!!, 500)
    }


    private fun initFB() {
        beautyList.clear()
        mAdapter = BeautiesAdapter()
        val stateAdListener: BeautiesAdapter.OnAdClickListener =
            object : BeautiesAdapter.OnAdClickListener {
                override fun onItemClick(beauty: Beauty, position: Int) {
                    if (beauty != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", beauty.firstImageURL)
                        bundle.putSerializable("image_second", beauty.secondImageURL)
                        bundle.putSerializable("image_third", beauty.thirdImageURL)
                        bundle.putSerializable("image_fourth", beauty.fourthImageURL)
                        bundle.putSerializable("text", beauty.text)
                        bundle.putSerializable("category", beauty.category)
                        bundle.putSerializable("date", beauty.timeStamp.toString())
                        bundle.putSerializable("title", beauty.title)
                        bundle.putSerializable("station", beauty.station)
                        bundle.putSerializable("salary", beauty.salary)
                        bundle.putSerializable("phone", beauty.phone_number)
                        bundle.putSerializable("likes", beauty.likes)
                        bundle.putSerializable("viewings", beauty.viewings)
                        bundle.putSerializable("id", beauty.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }
        GlobalScope.launch {
            mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_BEAUTY)
            mRefListener = AppValueEventListener {
                for (dataSnapshot: DataSnapshot in it.children) {
                    var beauty: Beauty = dataSnapshot.getValue(Beauty::class.java)!!

                    if (!beautyList.contains(beauty) && beauty != null) {
                        beautyList.add(0, beauty)
                    }
                    mAdapter.update(beautyList, stateAdListener)
                    mRecyclerView.adapter = mAdapter
                    mProgressBar.visibility = ProgressBar.INVISIBLE
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
            mapListeners[mRefAds] = mRefListener

        }
    }

    private fun initSearchFB(search_word: String) {
        mAdapter = BeautiesAdapter()
        val stateAdListener: BeautiesAdapter.OnAdClickListener =
            object : BeautiesAdapter.OnAdClickListener {
                override fun onItemClick(beauty: Beauty, position: Int) {
                    if (beauty != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", beauty.firstImageURL)
                        bundle.putSerializable("image_second", beauty.secondImageURL)
                        bundle.putSerializable("image_third", beauty.thirdImageURL)
                        bundle.putSerializable("image_fourth", beauty.fourthImageURL)
                        bundle.putSerializable("text", beauty.text)
                        bundle.putSerializable("category", beauty.category)
                        bundle.putSerializable("date", beauty.timeStamp.toString())
                        bundle.putSerializable("title", beauty.title)
                        bundle.putSerializable("station", beauty.station)
                        bundle.putSerializable("salary", beauty.salary)
                        bundle.putSerializable("likes", beauty.likes)
                        bundle.putSerializable("viewings", beauty.viewings)
                        bundle.putSerializable("id", beauty.id)


                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }
        mRefAds = FirebaseDatabase.getInstance().getReference(NODE_BEAUTY)
        mRefListener = AppValueEventListener {
            for (dataSnapshot: DataSnapshot in it.children) {
                var beauty: Beauty = dataSnapshot.getValue(Beauty::class.java)!!


                if (selected_station == null && beauty.text.contains(search_word, true)) {
                    beautyList.add(0, beauty)
                    mAdapter.update(beautyList, stateAdListener)
                }
                if (selected_station == beauty.station && beauty.text.contains(search_word, true)) {
                    beautyList.add(0, beauty)
                    mAdapter.update(beautyList, stateAdListener)
                }
                if (selected_station == beauty.station && search_word == selected_station) {
                    beautyList.add(0, beauty)
                    mAdapter.update(beautyList, stateAdListener)
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
                beautyList.clear()
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

}