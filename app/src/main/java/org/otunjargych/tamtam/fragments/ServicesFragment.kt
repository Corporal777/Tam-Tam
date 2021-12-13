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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.ServicesAdapter
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment
import org.otunjargych.tamtam.model.Services

class ServicesFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var dialog: MyDialogFragment
    private var selected_station: String? = null
    private lateinit var mEditTextSearch: EditText
    private lateinit var mAdapter: ServicesAdapter
    val servicesList: MutableList<Services> = ArrayList()
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

        val view: View = inflater.inflate(R.layout.fragment_services, container, false)

        mRecyclerView = view.findViewById(R.id.recycler_view_services)
        val mCardView: CardView = view.findViewById(R.id.card_metro)
        val mCardViewFirst: CardView = view.findViewById(R.id.card_services_first)
        val mCardViewSecond: CardView = view.findViewById(R.id.card_services_second)
        val mCardViewThird: CardView = view.findViewById(R.id.card_services_third)
        val mCardViewFourth: CardView = view.findViewById(R.id.card_services_fourth)
        val mImageViewBack: ImageView = view.findViewById(R.id.iv_back)
        val mImageViewAdd: ImageView = view.findViewById(R.id.iv_add)
        val mImageViewClear: ImageView = view.findViewById(R.id.iv_clear)
        mEditTextSearch = view.findViewById(R.id.et_search_services)
        mProgressBar = view.findViewById(R.id.progressbar)

        mRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        mProgressBar.visibility = ProgressBar.VISIBLE


        mImageViewClear.setOnClickListener {
            selected_station = null
            mEditTextSearch.text.clear()
            servicesList.clear()
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
                dialog.show(manager!!, "metro3")
            }
        }
        mCardViewFirst.setOnClickListener {
            val str = "Документ"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            servicesList.clear()
        }
        mCardViewSecond.setOnClickListener {
            val str = "Кредит"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            servicesList.clear()
        }
        mCardViewThird.setOnClickListener {
            val str = "Язык"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            servicesList.clear()
        }
        mCardViewFourth.setOnClickListener {
            val str = "Кафе, Ресторан"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            servicesList.clear()
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
        initSearch()
        if (!mEditTextSearch.text.isEmpty() || selected_station != null) {
            initSearchFB(mEditTextSearch.text.toString())
            servicesList.clear()
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
                servicesList.clear()
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

    fun setSelectedStation(selectedItem: String?) {
        selected_station = selectedItem!!
        mEditTextSearch.setText(selected_station)
        mHandler = Handler()
        mRunnable = Runnable {
            dialog.dismiss()
        }
        mHandler!!.postDelayed(mRunnable!!, 400)
    }

    private fun initFB() {
        mAdapter = ServicesAdapter()
        val stateAdListener: ServicesAdapter.OnAdClickListener =
            object : ServicesAdapter.OnAdClickListener {
                override fun onAdClick(services: Services, position: Int) {
                    if (services != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", services.imageURL)
                        bundle.putSerializable("text", services.text)
                        bundle.putSerializable("category", services.category)
                        bundle.putSerializable("date", services.timeStamp.toString())
                        bundle.putSerializable("title", services.title)
                        bundle.putSerializable("station", services.station)
                        bundle.putSerializable("salary", services.salary)
                        bundle.putSerializable("phone", services.phone_number)
                        bundle.putSerializable("likes", services.likes)
                        bundle.putSerializable("viewings", services.viewings)
                        bundle.putSerializable("id", services.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }
        CoroutineScope(Dispatchers.IO).launch {
            mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_SERVICES)
            mRefListener = AppValueEventListener {
                servicesList.clear()
                for (dataSnapshot: DataSnapshot in it.children) {
                    var services: Services =
                        dataSnapshot.getValue(Services::class.java)!!

                    if (!servicesList.contains(services)) {
                        servicesList.add(0, services)
                    }
                    mAdapter.update(servicesList, stateAdListener)
                    mRecyclerView.adapter = mAdapter
                    mProgressBar.visibility = View.INVISIBLE
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
            mapListeners[mRefAds] = mRefListener
        }
    }

    private fun initSearchFB(search_word: String) {
        mAdapter = ServicesAdapter()
        val stateAdListener: ServicesAdapter.OnAdClickListener =
            object : ServicesAdapter.OnAdClickListener {
                override fun onAdClick(services: Services, position: Int) {
                    if (services != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", services.imageURL)
                        bundle.putSerializable("text", services.text)
                        bundle.putSerializable("category", services.category)
                        bundle.putSerializable("date", services.timeStamp.toString())
                        bundle.putSerializable("title", services.title)
                        bundle.putSerializable("station", services.station)
                        bundle.putSerializable("salary", services.salary)
                        bundle.putSerializable("phone", services.phone_number)
                        bundle.putSerializable("likes", services.likes)
                        bundle.putSerializable("viewings", services.viewings)
                        bundle.putSerializable("id", services.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }

        mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_SERVICES)
        mRefListener = AppValueEventListener {
            for (dataSnapshot: DataSnapshot in it.children) {
                var services: Services =
                    dataSnapshot.getValue(Services::class.java)!!
                if (selected_station == null && services.text.contains(search_word, true)) {
                    servicesList.add(0, services)
                    mAdapter.update(servicesList, stateAdListener)
                }
                if (selected_station == services.station && services.text.contains(
                        search_word,
                        true
                    )
                ) {
                    servicesList.add(0, services)
                    mAdapter.update(servicesList, stateAdListener)
                }
                if (selected_station == services.station && search_word == selected_station) {
                    servicesList.add(0, services)
                    mAdapter.update(servicesList, stateAdListener)
                }
                mRecyclerView.adapter = mAdapter
                mProgressBar.visibility = ProgressBar.INVISIBLE
            }
        }
        mRefAds.addListenerForSingleValueEvent(mRefListener)
        mapListeners[mRefAds] = mRefListener
    }


}