package myapp.firstapp.tamtam.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import myapp.firstapp.tamtam.R
import myapp.firstapp.tamtam.activities.RegistrationActivity
import myapp.firstapp.tamtam.adapter.WorksAdapter
import myapp.firstapp.tamtam.extensions.*
import myapp.firstapp.tamtam.model.Work


class MainFragment : Fragment() {

    private lateinit var adapter: WorksAdapter
    val workList: MutableList<Work> = ArrayList()
    private lateinit var mButton: FloatingActionButton
    private lateinit var mNestedScrollView: NestedScrollView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    private var mCountAds = 15
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mBottomAppBar: BottomAppBar
    private var mIsScrolling = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)
        mRecyclerView = view.findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        val card_work: CardView = view.findViewById(R.id.card_work)
        val card_beauty: CardView = view.findViewById(R.id.card_medicine)
        val card_transport: CardView = view.findViewById(R.id.card_transport)
        val card_buy_sell: CardView = view.findViewById(R.id.card_buy_sell)
        val card_flats: CardView = view.findViewById(R.id.card_flats)
        val card_services: CardView = view.findViewById(R.id.card_services)
        mButton = view.findViewById(R.id.btn_add_ads)
        mNestedScrollView = view.findViewById(R.id.nested_scroll_view)
        mToolbar = view.findViewById(R.id.main_toolbar)
        mProgressBar = view.findViewById(R.id.progressbar)
        val mImageViewEnter: ImageView = view.findViewById(R.id.iv_enter)
        mBottomAppBar = view.findViewById(R.id.bottomBar)
        USER = FirebaseAuth.getInstance()
        setOnMenuItem()


        if (FirebaseAuth.getInstance().currentUser != null || !hasConnection(activity!!)) {
            mImageViewEnter.visibility = View.GONE
        }

        mImageViewEnter.setOnClickListener {
            startActivity(Intent(activity, RegistrationActivity::class.java))
            activity?.finish()
        }

        if (!hasConnection(activity!!)) {
            Snackbar.make(view, "Нет интернет соединения!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
        }

        mButton.setOnClickListener {
            initCurrentUser()
        }

        card_work.setOnClickListener {
            replaceFragment(WorksFragment())
        }

        card_beauty.setOnClickListener {
            replaceFragment(BeautyFragment())
        }

        card_transport.setOnClickListener {
            replaceFragment(TransportFragment())
        }
        card_buy_sell.setOnClickListener {
            replaceFragment(BuySellFragment())
        }
        card_flats.setOnClickListener {
            replaceFragment(FlatsFragment())
        }
        card_services.setOnClickListener {
            replaceFragment(ServicesFragment())
        }

        //initAdvertising()
        return view
    }

    private fun initCurrentUser() {
        AUTH = FirebaseAuth.getInstance()
        if (hasConnection(context!!)){
            if (AUTH.currentUser != null) {
                replaceFragment(AdFragment())
            }else {
                startActivity(Intent(activity, RegistrationActivity::class.java))
                activity?.finish()
            }
        }
        else {
            Snackbar.make(view!!, "Нет интернет соединения!", Snackbar.LENGTH_LONG)
                .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        initFB()
    }


    private fun initFB() {
        mRecyclerView.layoutManager = GridLayoutManager(activity, 2)
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

        CoroutineScope(Dispatchers.IO).launch {
            workList.clear()
            mRefAds = FirebaseDatabase.getInstance().getReference().child(NODE_WORKS)
            mRefListener = AppValueEventListener {
                for (dataSnapshot: DataSnapshot in it.children) {
                    var work: Work = dataSnapshot.getValue(Work::class.java)!!

                    if (!workList.contains(work)) {
                        workList.add(0, work)
                        adapter.update(workList, stateAdListener)
                    }
                    mProgressBar.visibility = ProgressBar.INVISIBLE
                }
                mRecyclerView.adapter = adapter
            }
            mRefAds.limitToLast(mCountAds).addListenerForSingleValueEvent(mRefListener)
            mapListeners[mRefAds] = mRefListener
            mRefAds.keepSynced(true)
        }


    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }


    private fun setOnMenuItem() {
        mBottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.liked_ads -> {
                    if (hasConnection(context!!) && USER.currentUser != null) {
                        replaceFragment(LikedAdsFragment())
                        true
                    } else
                        false
                }
                R.id.about_app -> {
                    replaceFragment(AboutAppFragment())
                    true
                }
                R.id.own_acc -> {
                    if (USER.currentUser != null) {
                        replaceFragment(AccountFragment())
                        true
                    } else
                        false
                }
                R.id.net_with_dev -> {
                    val url: String =
                        "https://api.whatsapp.com/send?phone=" + "+79267806176"
                    val i: Intent = Intent(Intent.ACTION_VIEW)
                    i.setData(Uri.parse(url))
                    startActivity(i)
                    true
                }
                else -> false
            }
        }
    }
}

