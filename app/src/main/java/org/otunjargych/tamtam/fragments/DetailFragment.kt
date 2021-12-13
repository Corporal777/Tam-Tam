package org.otunjargych.tamtam.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.ViewPagerAdapter
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.dialog_fragments.FullScreenImagesFragment
import org.otunjargych.tamtam.model.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DetailFragment : Fragment() {

    private lateinit var mViewPager: ViewPager2
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var singleImageList: MutableList<String>
    private lateinit var moreImageList: MutableList<String>
    private lateinit var mTextViewDetailText: TextView
    private lateinit var mTextViewDetailPhoneWhatsAppNumber: TextView
    private lateinit var mTextViewDetailPhoneNumber: TextView
    private lateinit var mTextViewDetailTitle: TextView
    private lateinit var mTextViewDetailSalary: TextView
    private lateinit var mTextViewDetailCategory: TextView
    private lateinit var mTextViewDetailDate: TextView
    private lateinit var mTextViewDetailLikes: TextView
    private lateinit var mTextViewDetailViewings: TextView
    private lateinit var mImageViewLike: ImageView
    private lateinit var mTextViewDetailStation: TextView
    private lateinit var mCardViewWhatsAppNumber: CardView
    private lateinit var mCardViewPhoneNumber: CardView
    private lateinit var mRefAds: DatabaseReference
    private lateinit var mRefListener: AppValueEventListener
    private lateinit var text: String
    private lateinit var date: String
    private var id: String = ""
    private lateinit var category: String
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()
    private lateinit var likedAds: LikedAds
    private lateinit var mImageViewAuthorPhoto: ImageView
    private lateinit var mTextViewAuthorName: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val mToolbar: MaterialToolbar = view.findViewById(R.id.main_toolbar_detail)

        mTextViewDetailText = view.findViewById(R.id.tv_text_detail)
        mTextViewDetailTitle = view.findViewById(R.id.tv_title_detail)
        mTextViewDetailCategory = view.findViewById(R.id.tv_category_detail)
        mTextViewDetailDate = view.findViewById(R.id.tv_date_detail)
        mTextViewDetailSalary = view.findViewById(R.id.tv_salary_detail)
        mTextViewDetailStation = view.findViewById(R.id.tv_location_detail)
        mTextViewDetailLikes = view.findViewById(R.id.tv_likes)
        mTextViewDetailViewings = view.findViewById(R.id.tv_viewings)
        mImageViewLike = view.findViewById(R.id.iv_like)
        mViewPager = view.findViewById(R.id.view_pager)
        mCardViewWhatsAppNumber = view.findViewById(R.id.btn_detail_whatsapp_number)
        mCardViewPhoneNumber = view.findViewById(R.id.btn_detail_phone_number)
        mTextViewDetailPhoneWhatsAppNumber = view.findViewById(R.id.tv_detail_phone_number_whatsapp)
        mTextViewDetailPhoneNumber = view.findViewById(R.id.tv_detail_phone_number)
        mTextViewAuthorName = view.findViewById(R.id.tv_detail_user_name)
        mImageViewAuthorPhoto = view.findViewById(R.id.iv_detail_user_photo)


        val tab: TabLayout = view.findViewById(R.id.tab_dots)
        val empty_image: Drawable? = resources.getDrawable(R.drawable.placeholder)
        val image_1: Serializable? = arguments?.getSerializable("image_first")
        val image_2: Serializable? = arguments?.getSerializable("image_second")
        val image_3: Serializable? = arguments?.getSerializable("image_third")
        val image_4: Serializable? = arguments?.getSerializable("image_fourth")
        text = arguments?.getSerializable("text") as String
        val title: Serializable? = arguments?.getSerializable("title")
        category = arguments?.getSerializable("category") as String
        val salary: Serializable? = arguments?.getSerializable("salary")
        val station: Serializable? = arguments?.getSerializable("station")
        date = arguments?.getSerializable("date") as String
        var phone: Serializable? = arguments?.getSerializable("phone")
        var likes: Serializable? = arguments?.getSerializable("likes")
        var viewings: Serializable? = arguments?.getSerializable("viewings")
        id = arguments?.getSerializable("id") as String

        likedAds = LikedAds(
            id,
            title.toString(),
            text,
            salary.toString(),
            station.toString(),
            date,
            category,
            likes as Int,
            viewings as Int,
            image_1.toString(),
            image_2.toString(),
            image_3.toString(),
            image_4.toString(),
            phone.toString()
        )

        mTextViewDetailTitle.text = title.toString()
        mTextViewDetailText.text = text.toString()
        mTextViewDetailCategory.text = category.toString()
        mTextViewDetailDate.text = date.toString().asTime()
        mTextViewDetailLikes.text = likes.toString()
        mTextViewDetailViewings.text = viewings.toString()

        if (salary == "" || salary == null) {
            mTextViewDetailSalary.text = "Договорная"
        } else
            mTextViewDetailSalary.text = salary.toString()
        if (station != null || station != "") {
            mTextViewDetailStation.text = "м. " + station.toString()
        } else
            mTextViewDetailStation.text = "Не указано"

        if (phone == "" || phone == null) {
            mTextViewDetailPhoneWhatsAppNumber.text = "Не указано"
            mTextViewDetailPhoneNumber.text = "Не указано"
        } else {
            mTextViewDetailPhoneWhatsAppNumber.text = phone.toString()
            mTextViewDetailPhoneNumber.text = phone.toString()
        }

        moreImageList = ArrayList()
        singleImageList = ArrayList()
        adapter = ViewPagerAdapter()

        val onImageClick = object : ViewPagerAdapter.OnImageClickListener{
            override fun onImageClick() {
                val bundle: Bundle = Bundle()
                bundle.putSerializable("image_first", image_1)
                bundle.putSerializable("image_second", image_2)
                bundle.putSerializable("image_third", image_3)
                bundle.putSerializable("image_fourth", image_4)

                val fragment: FullScreenImagesFragment = FullScreenImagesFragment()
                replaceFragment(fragment)
                fragment.arguments = bundle
            }
        }

        if (category == "Работа, Подработки" || category == "Транспорт, Перевозки") {
            singleImageList.add(image_1.toString())
            //adapter = ViewPagerAdapter(singleImageList)
            adapter.update(singleImageList, onImageClick)
        }
        if (category == "Квартиры, Гостиницы" || category == "Обучение, Услуги") {
            singleImageList.add(image_1.toString())
            adapter.update(singleImageList, onImageClick)
            //adapter = ViewPagerAdapter(singleImageList)
        }
        if (category == "Медицина, Красота" || category == "Продажа, Покупка") {
            if (image_1 != "" && image_1 != null) {
                moreImageList.add(image_1.toString())
            }
            if (image_2 != "" && image_2 != null) {
                moreImageList.add(image_2.toString())
            }
            if (image_3 != "" && image_3 != null) {
                moreImageList.add(image_3.toString())
            }
            if (image_4 != "" && image_4 != null) {
                moreImageList.add(image_4.toString())
            }
            //adapter = ViewPagerAdapter(moreImageList)
            adapter.update(moreImageList, onImageClick)
        }

        mViewPager.adapter = adapter
        TabLayoutMediator(tab, mViewPager) { tab, position ->
        }.attach()

        if (phone == "" || phone == null) {
            mCardViewPhoneNumber.isClickable = false
            mCardViewWhatsAppNumber.isClickable = false
        } else {
            mCardViewWhatsAppNumber.setOnClickListener {
                val firstNum = StringBuilder(phone.toString())[0]
                if (firstNum == '8') {
                    phone = StringBuilder(phone.toString()).deleteCharAt(0)
                    phone = "+7" + phone
                }
                val url: String = "https://api.whatsapp.com/send?phone=" + phone
                val i: Intent = Intent(Intent.ACTION_VIEW)
                i.setData(Uri.parse(url))
                startActivity(i)
            }

            mCardViewPhoneNumber.setOnClickListener {
                val url: String = "tel:" + phone.toString()
                val inn: Intent = Intent(Intent.ACTION_DIAL)
                inn.setData(Uri.parse(url))
                startActivity(inn)
            }
        }

        mImageViewLike.setOnClickListener {
            AUTH = FirebaseAuth.getInstance()
            if (AUTH.currentUser != null) {
                initLikes()
                mImageViewLike.setImageResource(R.drawable.ic_liked)
                mImageViewLike.isClickable = false
                val animScale: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_right)
                mImageViewLike.startAnimation(animScale)
                successToast("Добавлено в избранное", activity!!)
            } else
                errorToast("Создайте или войдите в аккаунт!", activity!!)

        }

        return view
    }


    private fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return timeFormat.format(time)
    }

    override fun onPause() {
        super.onPause()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }

    override fun onResume() {
        super.onResume()
        initViewings()
        initAuthor()
        if (FirebaseAuth.getInstance().currentUser != null) {
            checkLikedAds()
        }
    }

    private fun initAuthor() {

        val mRefUserAds =
            FirebaseDatabase.getInstance().getReference(NODE_USERS).child(id)
        mRefListener = AppValueEventListener { snapShot ->

            if (snapShot == null) {
                mTextViewAuthorName.setText("Не указано")
                mImageViewAuthorPhoto.load(R.drawable.placeholder)
            } else {
                val user: User? = snapShot.getValue(User::class.java)
                if (id != null || id != "") {
                    if (id == user?.id) {
                        mTextViewAuthorName.setText(user?.name + " " + user?.last_name)
                        mImageViewAuthorPhoto.load(user?.image)
                    }
                }
            }
        }
        mRefUserAds.addListenerForSingleValueEvent(mRefListener)
        mapListeners[mRefUserAds] = mRefListener
    }

    private fun initLikes() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val mRefUserAds =
            FirebaseDatabase.getInstance().getReference(NODE_USERS).child(uid!!)
                .child(NODE_LIKED_ADS)

        if (category == "Работа, Подработки") {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_WORKS)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val work: Work? = dataSnapshot.getValue(Work::class.java)
                    if (text?.equals(work?.text)!!) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(work!!.likes + 1)
                        mRefUserAds.child(date).setValue(likedAds)
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
                    if (text?.equals(beauty?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(beauty!!.likes + 1)
                        mRefUserAds.child(date).setValue(likedAds)
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
                    if (text.equals(transport?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(transport!!.likes + 1)
                        mRefUserAds.child(date).setValue(likedAds)
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
                    if (text.equals(buySell?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(buySell?.likes!! + 1)
                        mRefUserAds.child(date).setValue(likedAds)
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
                    if (text.equals(flats?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(flats?.likes!! + 1)
                        mRefUserAds.child(date).setValue(likedAds)
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
                    if (text.equals(services?.text)) {
                        val likes: DatabaseReference = dataSnapshot.ref.child("likes")
                        likes.setValue(services?.likes!! + 1)
                        mRefUserAds.child(date).setValue(likedAds)
                    }
                }
            }
            mRefAds.addListenerForSingleValueEvent(mRefListener)
        }
        mapListeners[mRefAds] = mRefListener
    }


    private fun initViewings() {
        if (category == "Работа, Подработки") {
            mRefAds = FirebaseDatabase.getInstance().getReference(NODE_WORKS)
            mRefListener = AppValueEventListener { snapShot ->
                for (dataSnapshot: DataSnapshot in snapShot.children) {
                    val work: Work? = dataSnapshot.getValue(Work::class.java)
                    if (text?.equals(work?.text)!!) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(work!!.viewings + 1)
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
                    if (text?.equals(beauty?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(beauty!!.viewings + 1)
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
                    if (text.equals(transport?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(transport!!.viewings + 1)
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
                    if (text.equals(buySell?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(buySell?.viewings!! + 1)
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
                    if (text.equals(flats?.text)) {
                        val viewings: DatabaseReference = dataSnapshot.ref.child("viewings")
                        viewings.setValue(flats?.viewings!! + 1)
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
                    if (text.equals(services?.text)) {
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
                if (text != null || likedAds != null) {
                    if (text.equals(likedAds?.text)) {
                        mImageViewLike.setImageResource(R.drawable.ic_liked)
                        mImageViewLike.isClickable = false
                    }
                }
            }
        }
        mRefUserAds.addListenerForSingleValueEvent(mRefListener)
        mapListeners[mRefUserAds] = mRefListener
    }
}