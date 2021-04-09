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
import android.widget.TextView.OnEditorActionListener
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_work.view.*
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import myapp.firstapp.tamtam.R
import myapp.firstapp.tamtam.adapter.WorksAdapter
import myapp.firstapp.tamtam.extensions.*
import myapp.firstapp.tamtam.fragments.dialog_fragments.MyDialogFragment
import myapp.firstapp.tamtam.model.Work
import java.util.*
import kotlin.collections.ArrayList

class WorksFragment : Fragment() {


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
    private lateinit var adapter: WorksAdapter
    val workList: MutableList<Work> = ArrayList()
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

        val view: View = inflater.inflate(R.layout.fragment_work, container, false)

        mRecyclerView = view.findViewById(R.id.recycler_view_first_page)
        mCardView = view.findViewById(R.id.card_metro)
        mImageViewBack = view.findViewById(R.id.iv_back)
        mImageViewAdd = view.findViewById(R.id.iv_add)
        mEditTextSearch = view.findViewById(R.id.et_search_work)
        mImageViewClear = view.findViewById(R.id.iv_clear)
        mCardViewFirst = view.findViewById(R.id.card_work_first)
        mCardViewSecond = view.findViewById(R.id.card_work_second)
        mCardViewThird = view.findViewById(R.id.card_work_third)
        mProgressBar = view.findViewById(R.id.progressbar)
        mProgressBar.visibility = ProgressBar.VISIBLE
        mRecyclerView.layoutManager = GridLayoutManager(activity, 2)

        mImageViewClear.setOnClickListener {
            selected_station = null
            mEditTextSearch.text.clear()
            workList.clear()
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
                val manager : FragmentManager? = fragmentManager
                dialog = MyDialogFragment()
                dialog.show(manager!!, "metro2")
            }
        }

        mCardViewFirst.setOnClickListener {
            val str = "Подработ"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            workList.clear()
        }
        mCardViewSecond.setOnClickListener {
            val str = "Постоянн"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            workList.clear()
        }
        mCardViewThird.setOnClickListener {
            val str = "Ищу работу"
            initSearchFB(str)
            mEditTextSearch.setText(str)
            workList.clear()
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
            workList.clear()
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


    private fun initSearchFB(search_word: String) {
        adapter = WorksAdapter()
        val stateAdListener: WorksAdapter.OnAdClickListener =
            object : WorksAdapter.OnAdClickListener {
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
                        bundle.putSerializable("likes", work.likes)
                        bundle.putSerializable("viewings", work.viewings)
                        bundle.putSerializable("id", work.id)

                        val fragment: DetailFragment = DetailFragment()
                        replaceFragment(fragment)
                        fragment.arguments = bundle
                    }
                }
            }
        mRefAds = FirebaseDatabase.getInstance().getReference("works")
        mRefListener = AppValueEventListener {
            for (dataSnapshot: DataSnapshot in it.children) {
                var work: Work = dataSnapshot.getValue(Work::class.java)!!
                if (selected_station == null && work.text.contains(search_word, true)) {
                    workList.add(0, work)
                    adapter.update(workList, stateAdListener)
                }
                if (selected_station == work.station && work.text.contains(search_word, true)) {
                    workList.add(0, work)
                    adapter.update(workList, stateAdListener)
                }
                if (selected_station == work.station && search_word == selected_station) {
                    workList.add(0, work)
                    adapter.update(workList, stateAdListener)
                }
                mRecyclerView.adapter = adapter
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
        mEditTextSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                initSearchFB(mEditTextSearch.text.toString())
                performSearch()
                workList.clear()
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

    private fun initFB() {
        workList.clear()
        adapter = WorksAdapter()
        val stateAdListener: WorksAdapter.OnAdClickListener =
            object : WorksAdapter.OnAdClickListener {
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
        GlobalScope.launch {
            mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_WORKS)
            mRefListener = AppValueEventListener {
                for (dataSnapshot: DataSnapshot in it.children) {
                    var work: Work = dataSnapshot.getValue(Work::class.java)!!

                    if (!workList.contains(work) && work != null) {
                        workList.add(0, work)
                    }
                    adapter.update(workList, stateAdListener)
                    mRecyclerView.adapter = adapter
                    mProgressBar.visibility = ProgressBar.INVISIBLE
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