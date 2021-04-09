package myapp.firstapp.tamtam.fragments

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
import myapp.firstapp.tamtam.model.Flats
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import myapp.firstapp.tamtam.R
import myapp.firstapp.tamtam.adapter.FlatsAdapter
import myapp.firstapp.tamtam.extensions.*
import myapp.firstapp.tamtam.fragments.dialog_fragments.MyDialogFragment

class FlatsFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var dialog: MyDialogFragment
    private var selected_station: String? = null
    private lateinit var mEditTextSearch: EditText
    private lateinit var mAdapter: FlatsAdapter
    val flatsList: MutableList<Flats> = ArrayList()
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
        val view: View = inflater.inflate(R.layout.fragment_flats, container, false)
        mRecyclerView = view.findViewById(R.id.recycler_view_flats)
        val mCardView: CardView = view.findViewById(R.id.card_metro)
        val mCardViewFirst: CardView = view.findViewById(R.id.card_flats_first)
        val mCardViewSecond: CardView = view.findViewById(R.id.card_flats_second)
        val mCardViewThird: CardView = view.findViewById(R.id.card_flats_third)
        val mCardViewFourth: CardView = view.findViewById(R.id.card_flats_fourth)
        val mImageViewBack: ImageView = view.findViewById(R.id.iv_back)
        val mImageViewAdd: ImageView = view.findViewById(R.id.iv_add)
        val mImageViewClear: ImageView = view.findViewById(R.id.iv_clear)
        mEditTextSearch = view.findViewById(R.id.et_search_flats)
        mProgressBar = view.findViewById(R.id.progressbar)

        mRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        mProgressBar.visibility = ProgressBar.VISIBLE

        mImageViewClear.setOnClickListener {
            selected_station = null
            mEditTextSearch.text.clear()
            flatsList.clear()
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
            val str = "Квартир"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            flatsList.clear()
        }
        mCardViewSecond.setOnClickListener {
            val str = "Комнат"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            flatsList.clear()
        }
        mCardViewThird.setOnClickListener {
            val str = "Койко Место"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            flatsList.clear()
        }
        mCardViewFourth.setOnClickListener {
            val str = "Гостиница"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            flatsList.clear()
        }
        return view
    }

    private fun initCurrentUser() {
        AUTH = FirebaseAuth.getInstance()
        if (hasConnection(context!!)){
            if (AUTH.currentUser != null) {
                replaceFragment(AdFragment())
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
            flatsList.clear()
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
                flatsList.clear()
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
        mAdapter = FlatsAdapter()
        val stateAdListener: FlatsAdapter.OnAdClickListener =
            object : FlatsAdapter.OnAdClickListener {
                override fun onAdClick(flats: Flats, position: Int) {
                    if (flats != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", flats.imageURL)
                        bundle.putSerializable("text", flats.text)
                        bundle.putSerializable("category", flats.category)
                        bundle.putSerializable("date", flats.timeStamp.toString())
                        bundle.putSerializable("title", flats.title)
                        bundle.putSerializable("station", flats.station)
                        bundle.putSerializable("salary", flats.salary)
                        bundle.putSerializable("phone", flats.phone_number)
                        bundle.putSerializable("likes", flats.likes)
                        bundle.putSerializable("viewings", flats.viewings)
                        bundle.putSerializable("id", flats.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }
        CoroutineScope(Dispatchers.IO).launch {
            mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_HOUSE)
            mRefListener = AppValueEventListener {
                flatsList.clear()
                for (dataSnapshot: DataSnapshot in it.children) {
                    var flats: Flats =
                        dataSnapshot.getValue(Flats::class.java)!!

                    if (!flatsList.contains(flats)) {
                        flatsList.add(0, flats)
                    }
                    mAdapter.update(flatsList, stateAdListener)
                    mRecyclerView.adapter = mAdapter
                    mProgressBar.visibility = View.INVISIBLE
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
            mapListeners[mRefAds] = mRefListener
        }
    }

    private fun initSearchFB(search_word: String) {
        mAdapter = FlatsAdapter()
        val stateAdListener: FlatsAdapter.OnAdClickListener =
            object : FlatsAdapter.OnAdClickListener {
                override fun onAdClick(flats: Flats, position: Int) {
                    if (flats != null) {
                        val bundle: Bundle = Bundle()
                        bundle.putSerializable("image_first", flats.imageURL)
                        bundle.putSerializable("text", flats.text)
                        bundle.putSerializable("category", flats.category)
                        bundle.putSerializable("date", flats.timeStamp.toString())
                        bundle.putSerializable("title", flats.title)
                        bundle.putSerializable("station", flats.station)
                        bundle.putSerializable("salary", flats.salary)
                        bundle.putSerializable("phone", flats.phone_number)
                        bundle.putSerializable("likes", flats.likes)
                        bundle.putSerializable("viewings", flats.viewings)
                        bundle.putSerializable("id", flats.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }

        mRefAds = FirebaseDatabase.getInstance().getReference(NODE_HOUSE)
        mRefListener = AppValueEventListener {
            for (dataSnapshot: DataSnapshot in it.children) {
                var flats: Flats = dataSnapshot.getValue(Flats::class.java)!!
                if (selected_station == null && flats.text.contains(search_word, true)) {
                    flatsList.add(0, flats)
                    mAdapter.update(flatsList, stateAdListener)
                }
                if (selected_station == flats.station && flats.text.contains(search_word, true)) {
                    flatsList.add(0, flats)
                    mAdapter.update(flatsList, stateAdListener)
                }
                if (selected_station == flats.station && search_word == selected_station) {
                    flatsList.add(0, flats)
                    mAdapter.update(flatsList, stateAdListener)
                }
                mRecyclerView.adapter = mAdapter
                mProgressBar.visibility = ProgressBar.INVISIBLE
            }
        }
        mRefAds.addListenerForSingleValueEvent(mRefListener)
        mapListeners[mRefAds] = mRefListener
    }

}