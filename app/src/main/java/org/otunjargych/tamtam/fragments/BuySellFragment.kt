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
import org.otunjargych.tamtam.adapter.BuySellAdapter
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment
import org.otunjargych.tamtam.model.BuySell

class BuySellFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mCardView: CardView
    private lateinit var dialog: MyDialogFragment
    private var selected_station: String? = null
    private lateinit var mImageViewBack: ImageView
    private lateinit var mImageViewAdd: ImageView
    private lateinit var mImageViewClear: ImageView
    private lateinit var mEditTextSearch: EditText
    private lateinit var mCardViewFirst: CardView
    private lateinit var mCardViewSecond: CardView
    private lateinit var mCardViewThird: CardView
    private lateinit var mCardViewFourth: CardView
    private lateinit var mAdapter: BuySellAdapter
    val buySellList: MutableList<BuySell> = ArrayList()
    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    private lateinit var mProgressBar: ProgressBar

    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_buy_sell, container, false)
        mRecyclerView = view.findViewById(R.id.recycler_view_buy_sell)
        mRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        mCardView = view.findViewById(R.id.card_metro)
        mImageViewAdd = view.findViewById(R.id.iv_add)
        mImageViewClear = view.findViewById(R.id.iv_clear)
        mEditTextSearch = view.findViewById(R.id.et_search_buy_sell)
        mCardViewFirst = view.findViewById(R.id.card_buy_sell_first)
        mCardViewSecond = view.findViewById(R.id.card_buy_sell_second)
        mCardViewThird = view.findViewById(R.id.card_buy_sell_third)
        mCardViewFourth = view.findViewById(R.id.card_buy_sell_fourth)
        mProgressBar = view.findViewById(R.id.progressbar)

        mImageViewClear.setOnClickListener {
            selected_station = null
            mEditTextSearch.text.clear()
            buySellList.clear()
            initFB()
        }
        mImageViewBack.setOnClickListener {
            fragmentManager?.popBackStack()
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
                dialog.show(manager!!, "metro4")
            }
        }
        mCardViewFirst.setOnClickListener {
            val str = "Телефон"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            buySellList.clear()
        }
        mCardViewSecond.setOnClickListener {
            val str = "Авто, Машина"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            buySellList.clear()
        }
        mCardViewThird.setOnClickListener {
            val str = "Дом, Мебель"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            buySellList.clear()
        }
        mCardViewFourth.setOnClickListener {
            val str = "Обмен"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            buySellList.clear()
        }

        return view
    }


    private fun initCurrentUser() {
        AUTH = FirebaseAuth.getInstance()
        if (hasConnection(context!!)) {
            if (AUTH.currentUser != null) {
                replaceFragment(NewAdFragment())
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
            buySellList.clear()
            initSearch()
        } else {
            initFB()
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
        buySellList.clear()
        mAdapter = BuySellAdapter()
        val stateAdListener: BuySellAdapter.OnAdClickListener =
            object : BuySellAdapter.OnAdClickListener {
                override fun onAdClick(buySell: BuySell, position: Int) {
                    if (buySell != null) {

                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", buySell.firstImageURL)
                        bundle.putSerializable("image_second", buySell.secondImageURL)
                        bundle.putSerializable("image_third", buySell.thirdImageURL)
                        bundle.putSerializable("image_fourth", buySell.fourthImageURL)
                        bundle.putSerializable("text", buySell.text)
                        bundle.putSerializable("category", buySell.category)
                        bundle.putSerializable("date", buySell.timeStamp.toString())
                        bundle.putSerializable("title", buySell.title)
                        bundle.putSerializable("station", buySell.station)
                        bundle.putSerializable("salary", buySell.salary)
                        bundle.putSerializable("phone", buySell.phone_number)
                        bundle.putSerializable("likes", buySell.likes)
                        bundle.putSerializable("viewings", buySell.viewings)
                        bundle.putSerializable("id", buySell.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }
        GlobalScope.launch {
            mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_BUY_SELL)
            mRefListener = AppValueEventListener {
                for (dataSnapshot: DataSnapshot in it.children) {
                    var buySell: BuySell = dataSnapshot.getValue(BuySell::class.java)!!

                    if (!buySellList.contains(buySell) && buySell != null) {
                        buySellList.add(0, buySell)
                    }
                    mAdapter.update(buySellList, stateAdListener)
                    mRecyclerView.adapter = mAdapter
                    mProgressBar.visibility = ProgressBar.INVISIBLE
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
            mapListeners[mRefAds] = mRefListener

        }
    }

    private fun initSearchFB(search_word: String) {
        mAdapter = BuySellAdapter()
        val stateAdListener: BuySellAdapter.OnAdClickListener =
            object : BuySellAdapter.OnAdClickListener {
                override fun onAdClick(buySell: BuySell, position: Int) {
                    if (buySell != null) {

                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", buySell.firstImageURL)
                        bundle.putSerializable("image_second", buySell.secondImageURL)
                        bundle.putSerializable("image_third", buySell.thirdImageURL)
                        bundle.putSerializable("image_fourth", buySell.fourthImageURL)
                        bundle.putSerializable("text", buySell.text)
                        bundle.putSerializable("category", buySell.category)
                        bundle.putSerializable("date", buySell.timeStamp.toString())
                        bundle.putSerializable("title", buySell.title)
                        bundle.putSerializable("station", buySell.station)
                        bundle.putSerializable("salary", buySell.salary)
                        bundle.putSerializable("phone", buySell.phone_number)
                        bundle.putSerializable("likes", buySell.likes)
                        bundle.putSerializable("viewings", buySell.viewings)
                        bundle.putSerializable("id", buySell.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }
        GlobalScope.launch {
            mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_BUY_SELL)
            mRefListener = AppValueEventListener {
                for (dataSnapshot: DataSnapshot in it.children) {
                    var buySell: BuySell = dataSnapshot.getValue(BuySell::class.java)!!

                    if (selected_station == null && buySell.text.contains(search_word, true)) {
                        buySellList.add(0, buySell)
                        mAdapter.update(buySellList, stateAdListener)
                    }
                    if (selected_station == buySell.station && buySell.text.contains(
                            search_word,
                            true
                        )
                    ) {
                        buySellList.add(0, buySell)
                        mAdapter.update(buySellList, stateAdListener)
                    }
                    if (selected_station == buySell.station && search_word == selected_station) {
                        buySellList.add(0, buySell)
                        mAdapter.update(buySellList, stateAdListener)
                    }
                    mRecyclerView.adapter = mAdapter
                    mProgressBar.visibility = ProgressBar.INVISIBLE
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
            mapListeners[mRefAds] = mRefListener

        }

    }

    private fun initSearch() {
        mEditTextSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                initSearchFB(mEditTextSearch.text.toString())
                performSearch()
                buySellList.clear()
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

    private fun performSearch() {
        mEditTextSearch.clearFocus()
        val inn: InputMethodManager? =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inn?.hideSoftInputFromWindow(mEditTextSearch.windowToken, 0)

    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }


}