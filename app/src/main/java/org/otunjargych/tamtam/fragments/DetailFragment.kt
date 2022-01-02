package org.otunjargych.tamtam.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.ViewPagerAdapter
import org.otunjargych.tamtam.databinding.FragmentDetailBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.model.*
import org.otunjargych.tamtam.viewmodel.UserViewModel
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
    private var mDetailWhatsapp: String = ""
    private var mDetailLikes: String = ""
    private var mDetailViewings: String = ""
    private var mDetailId: String = ""
    private var mDetailUserId: String = ""
    private var mDetailAddress: String = ""
    private var imagesList = ArrayList<String>()

    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    private val mViewModel: UserViewModel by activityViewModels()

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
        initAuthor()
    }

    private fun initFields() {
        adapter = ViewPagerAdapter()

        val onImageClick: ViewPagerAdapter.OnImageClickListener =
            object : ViewPagerAdapter.OnImageClickListener {
                override fun onImageClick() {
                }

            }
        adapter.update(imagesList, onImageClick)
        binding.viewPager.adapter = adapter
        binding.viewPager.setPageTransformer(DepthPageTransformer())
        TabLayoutMediator(binding.tabDots, binding.viewPager) { tab, position ->
        }.attach()

        binding.apply {
            tvCategoryDetail.text = mDetailCategory
            tvTitle.text = mDetailTitle
            tvText.text = mDetailText
            tvDate.text = mDetailDate.asTime()
            tvLikes.text = mDetailLikes
            tvViewings.text = mDetailViewings
            tvAddress.text = mDetailAddress

            if (!mDetailSalary.isNullOrEmpty()) {
                tvSalary.text = mDetailSalary
            }
            if (!mDetailStation.isNullOrEmpty()) {
                tvLocation.text = "м. $mDetailStation"
            }
            Boom(btnDetailPhoneNumber)
            Boom(btnDetailWhatsappNumber)
            if (!mDetailPhone.isNullOrEmpty()) {
                tvDetailPhoneNumber.text = mDetailPhone
                btnDetailPhoneNumber.setOnClickListener {
                    val url: String = "tel:$mDetailPhone"
                    val inn: Intent = Intent(Intent.ACTION_DIAL)
                    inn.data = Uri.parse(url)
                    startActivity(inn)
                }
            }
            if (!mDetailWhatsapp.isNullOrEmpty()) {
                tvDetailPhoneNumberWhatsapp.text = mDetailWhatsapp
                btnDetailWhatsappNumber.setOnClickListener {
                    val firstNum = StringBuilder(mDetailWhatsapp.toString())[0]
                    if (firstNum == '8') {
                        mDetailWhatsapp =
                            StringBuilder(mDetailWhatsapp.toString()).deleteCharAt(0).toString()
                        mDetailWhatsapp = "+7$mDetailWhatsapp"
                    }
                    val url: String = "https://api.whatsapp.com/send?phone=" + mDetailWhatsapp
                    val i: Intent = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
            }
            btnLike.setOnClickListener {
                if (AUTH.currentUser != null) {
                    //initLikes()
                    btnLike.setImageResource(R.drawable.ic_liked)
                    btnLike.isClickable = false
//                    val animScale: Animation =
//                        AnimationUtils.loadAnimation(requireContext(), R.anim.scale)
//                    btnLike.startAnimation(animScale)
                    snackMessage(requireContext(), view, "Добавлено в избранное")
                } else {
                    toastMessage(requireContext(), "Необходимо авторизоваться!")
                }

            }
            toolbarDetail.setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun initAuthor() {
        mViewModel.user.observe(viewLifecycleOwner, { state ->
            when (state) {
                is State.Loading -> {

                }
                is State.Success -> {
                    state.data.let { snapShot ->
                        if (snapShot != null) {
                            val user: User? = snapShot.getValue(User::class.java)
                            if (user != null) {
                                binding.tvDetailUserName.text = user.name + " " + user.last_name
                                binding.ivDetailUserPhoto.load(user.image)
                            }

                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        //initViewings()
        if (AUTH.currentUser != null) {
            checkLikedAds()
        }
        if (hasConnection(requireContext())) {
            mViewModel.loadUserData(mDetailUserId)
        }
    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }


    private fun initLikes() {

        val mRefUserAds =
            FirebaseDatabase.getInstance().getReference(NODE_USERS).child(USER_ID)
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
        imagesList.add("https://firebasestorage.googleapis.com/v0/b/tam-tam-8b2a7.appspot.com/o/notes_images%2Fplaceholder.png?alt=media&token=c8c79ca4-a95c-465e-9b06-f0c7d6ed5c91")
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
                mDetailWhatsapp = note.whatsapp_number
                mDetailLikes = note.likes.toString()
                mDetailViewings = note.viewings.toString()
                mDetailId = note.uuid
                mDetailAddress = note.addres
                mDetailUserId = note.userId
                if (!note.images.isNullOrEmpty() && note.images.size > 0) {
                    imagesList.clear()
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