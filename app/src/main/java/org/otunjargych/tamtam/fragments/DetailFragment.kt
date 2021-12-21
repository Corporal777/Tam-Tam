package org.otunjargych.tamtam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.ViewPagerAdapter
import org.otunjargych.tamtam.databinding.FragmentDetailBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.model.*
import java.util.*


class DetailFragment : BaseFragment() {

    private lateinit var adapter: ViewPagerAdapter
    private lateinit var category: String
    private lateinit var likedAds: LikedAds

    private var mDetailText: String = ""
    private var mDetailTitle: String = ""
    private var mDetailCategory: String = ""
    private var mDetailSalary: String = ""
    private var mDetailStation: String = ""
    private var mDetailDate: String = ""
    private var mDetailPhone: String = ""
    private var mDetailLikes: String = ""
    private var mDetailViewings: String = ""
    private var mDetailId: String = ""
    private var imagesList = ArrayList<String>()

    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDetailBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFields()
    }

    private fun initFields() {
        adapter = ViewPagerAdapter()

        val onImageClick: ViewPagerAdapter.OnImageClickListener = object : ViewPagerAdapter.OnImageClickListener{
            override fun onImageClick() {
            }

        }
        adapter.update(imagesList, onImageClick)
        binding.viewPager.adapter = adapter
        binding.viewPager.setPageTransformer(DepthPageTransformer())
        TabLayoutMediator(binding.tabDots, binding.viewPager) { tab, position ->
        }.attach()

        AUTH = FirebaseAuth.getInstance()
        UUID = FirebaseAuth.getInstance().currentUser?.uid.toString()
        binding.apply {
            tvCategoryDetail.text = mDetailCategory
            tvTitle.text = mDetailTitle
            tvText.text = mDetailText
            tvDate.text = mDetailDate.asTime()
            tvLikes.text = mDetailLikes
            tvViewings.text = mDetailViewings

            if (!mDetailSalary.isNullOrEmpty()) {
                tvSalary.text = mDetailSalary
            }
            if (!mDetailStation.isNullOrEmpty()) {
                tvLocation.text = "м. $mDetailStation"
            }
            if (!mDetailPhone.isNullOrEmpty()) {
                tvDetailPhoneNumber.text = mDetailPhone
                tvDetailPhoneNumberWhatsapp.text = mDetailPhone
            }
            btnLike.setOnClickListener {
                if (AUTH.currentUser != null) {
                    //initLikes()
                    btnLike.setImageResource(R.drawable.ic_liked)
                    btnLike.isClickable = false
//                    val animScale: Animation =
//                        AnimationUtils.loadAnimation(requireContext(), R.anim.scale)
//                    btnLike.startAnimation(animScale)
                    snackMessage(view, "Добавлено в избранное")
                } else {
                    toastMessage(requireContext(), "Необходимо авторизоваться!")
                }

            }
            toolbarDetail.setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        //initViewings()
        if (AUTH.currentUser != null) {
            checkLikedAds()
        }
    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }


    private fun initAuthor() {

    }

    private fun initLikes() {

        val mRefUserAds =
            FirebaseDatabase.getInstance().getReference(NODE_USERS).child(UUID)
                .child(NODE_LIKED_ADS)

        if (category == "Работа, Подработки") {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_WORKS)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val work: Work? = dataSnapshot.getValue(Work::class.java)
                    if (mDetailText?.contentEquals(work?.text)!!) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(work!!.likes + 1)
                        mRefUserAds.child(mDetailDate).setValue(likedAds)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }

        if (category == "Медицина, Красота") {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_BEAUTY)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val beauty: Beauty? = dataSnapshot.getValue(Beauty::class.java)
                    if (mDetailText?.contentEquals(beauty?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(beauty!!.likes + 1)
                        mRefUserAds.child(mDetailDate).setValue(likedAds)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        if (category == "Транспорт, Перевозки") {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_TRANSPORT)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val transport: Transportation? =
                        dataSnapshot.getValue(Transportation::class.java)
                    if (mDetailText.contentEquals(transport?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(transport!!.likes + 1)
                        mRefUserAds.child(mDetailDate).setValue(likedAds)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        if (category == "Продажа, Покупка") {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_BUY_SELL)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val buySell: BuySell? = dataSnapshot.getValue(BuySell::class.java)
                    if (mDetailText.contentEquals(buySell?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(buySell?.likes!! + 1)
                        mRefUserAds.child(mDetailDate).setValue(likedAds)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        if (category == "Квартиры, Гостиницы") {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_HOUSE)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val flats: Flats? = dataSnapshot.getValue(Flats::class.java)
                    if (mDetailText.contentEquals(flats?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(flats?.likes!! + 1)
                        mRefUserAds.child(mDetailDate).setValue(likedAds)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        if (category == "Обучение, Услуги") {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_SERVICES)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val services: Services? = dataSnapshot.getValue(Services::class.java)
                    if (mDetailText.contentEquals(services?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(services?.likes!! + 1)
                        mRefUserAds.child(mDetailDate).setValue(likedAds)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        mapListeners[mRefAds] = mRefListener
    }


    private fun initViewings() {
        if (category.contentEquals("Работа, Подработки")) {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_WORKS)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val work: Work? = dataSnapshot.getValue(Work::class.java)
                    if (mDetailText.contentEquals(work?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(work!!.viewings + 1)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        if (category.contentEquals("Медицина, Красота")) {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_BEAUTY)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val beauty: Beauty? = dataSnapshot.getValue(Beauty::class.java)
                    if (mDetailText.contentEquals(beauty?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(beauty!!.viewings + 1)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        if (category.contentEquals("Транспорт, Перевозки")) {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_TRANSPORT)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val transport: Transportation? =
                        dataSnapshot.getValue(Transportation::class.java)
                    if (mDetailText.contentEquals(transport?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(transport!!.viewings + 1)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        if (category.contentEquals("Продажа, Покупка")) {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_BUY_SELL)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val buySell: BuySell? = dataSnapshot.getValue(BuySell::class.java)
                    if (mDetailText.contentEquals(buySell?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(buySell?.viewings!! + 1)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        if (category.contentEquals("Квартиры, Гостиницы")) {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_HOUSE)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val flats: Flats? = dataSnapshot.getValue(Flats::class.java)
                    if (mDetailText.contentEquals(flats?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(flats?.viewings!! + 1)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        if (category.contentEquals("Обучение, Услуги")) {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_SERVICES)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val services: Services? = dataSnapshot.getValue(Services::class.java)
                    if (mDetailText.contentEquals(services?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(services?.viewings!! + 1)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        mapListeners[mRefAds] = mRefListener
    }

    private fun checkLikedAds() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val mRefUserAds =
            FirebaseDatabase.getInstance().getReference(NODE_USERS).child(uid!!)
                .child(NODE_LIKED_ADS)
        mRefListener = AppValueEventListener {
            for (dataSnapshot: DataSnapshot in it.children) {
                val likedAds: LikedAds? = dataSnapshot.getValue(LikedAds::class.java)
                if (!mDetailText.isNullOrEmpty()) {
                    if (likedAds != null) {
                        if (mDetailText.contentEquals(likedAds?.text)) {
                            binding.btnLike.setImageResource(R.drawable.ic_liked)
                            binding.btnLike.isClickable = false
                        }
                    }

                }
            }
        }
        mRefUserAds.addListenerForSingleValueEvent(mRefListener)
        mapListeners[mRefUserAds] = mRefListener
    }

    private fun getFields() {
        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            val note: Note? = bundle.getParcelable("note")
            if (note != null) {
                mDetailTitle = note.title
                mDetailText = note.text
                mDetailCategory = note.category
                mDetailSalary = note.salary
                mDetailStation = note.station
                mDetailDate = note.timeStamp.toString()
                mDetailPhone = note.phone_number
                mDetailLikes = note.likes.toString()
                mDetailViewings = note.viewings.toString()
                mDetailId = note.uuid
                if (!note.images.isNullOrEmpty()){
                    imagesList.addAll(note.images)
                }
                initFields()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}