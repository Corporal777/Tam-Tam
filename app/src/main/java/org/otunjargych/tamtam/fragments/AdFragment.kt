package org.otunjargych.tamtam.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.fragments.dialog_fragments.CategoryDialogFragment

import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment
import org.otunjargych.tamtam.model.*
import java.util.*


class AdFragment : Fragment() {

    private lateinit var mRefAds: DatabaseReference
    private lateinit var et_title: EditText
    private lateinit var et_salary: EditText
    private lateinit var et_text: EditText
    private lateinit var et_phone_number: EditText
    private lateinit var btn_add: Button
    private lateinit var tv_add_station: TextView
    private lateinit var tv_add_category: TextView
    private lateinit var mImageViewFirstImage: ImageView
    private lateinit var mImageViewSecondImage: ImageView
    private lateinit var mImageViewThirdImage: ImageView
    private lateinit var mImageViewFourthImage: ImageView
    private lateinit var mImageViewBack: ImageView
    private lateinit var mLinearLayout: LinearLayout
    private lateinit var dialog: MyDialogFragment
    private lateinit var dialog_category: CategoryDialogFragment
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null

    //URLs of images and category
    private var firstImageURL: String = ""
    private var secondImageURL: String = ""
    private var thirdImageURL: String = ""
    private var fourthImageURL: String = ""
    private lateinit var uid: String
    private var selected_station: String? = ""
    private var category: String? = ""

    //Request codes
    private var REQUEST_CODE_FOR_SINGLE_IMAGE = 1
    private var REQUEST_CODE_FOR_SECOND_IMAGES = 2
    private var REQUEST_CODE_FOR_THIRD_IMAGE = 3
    private var REQUEST_CODE_FOR_FOURTH_IMAGE = 4


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_new_ad, container, false)

        et_title = view.findViewById(R.id.work_title)
        et_text = view.findViewById(R.id.work_text)
        et_salary = view.findViewById(R.id.work_salary)
        tv_add_category = view.findViewById(R.id.tv_choose_category)
        et_phone_number = view.findViewById(R.id.work_phone_number)
        mImageViewFirstImage = view.findViewById(R.id.iv_photo_of_work)
        mImageViewSecondImage = view.findViewById(R.id.iv_second_image)
        mImageViewThirdImage = view.findViewById(R.id.iv_third_image)
        mImageViewFourthImage = view.findViewById(R.id.iv_fourth_image)
        mLinearLayout = view.findViewById(R.id.ln_of_other_images)
        mImageViewBack = view.findViewById(R.id.iv_back)
        btn_add = view.findViewById(R.id.btn_add_new_ad)
        tv_add_station = view.findViewById(R.id.tv_choose_metro)


        mImageViewBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        tv_add_station.setOnClickListener {
            val manager: FragmentManager? = fragmentManager
            if (!hasConnection(context!!)) {
                Snackbar.make(view!!, "Нет интернет соединения!", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(resources.getColor(R.color.app_main_color)).show()
            } else {
                dialog = MyDialogFragment()
                dialog.show(manager!!, "metro")
            }

        }

        tv_add_category.setOnClickListener {
            val manager: FragmentManager? = fragmentManager
            dialog_category = CategoryDialogFragment()
            dialog_category.show(manager!!, "category")
        }

        btn_add.setOnClickListener {

            if (et_text.text.isEmpty() || category.toString().isEmpty()) {
                errorToast("Заполните все поля!", activity!!)

            } else {
                addToFB()
                Snackbar.make(it, "Объявление добавлено", Snackbar.LENGTH_LONG)
                    .setAction("Назад") {
                        fragmentManager?.popBackStack()
                    }.setBackgroundTint(resources.getColor(R.color.app_main_color))
                    .setActionTextColor(resources.getColor(R.color.action_color))
                    .show()
                mHandler = Handler()
                mRunnable = Runnable {
                    fragmentManager?.popBackStack()
                }
                mHandler!!.postDelayed(mRunnable!!, 1500)
            }


        }

        mImageViewFirstImage.setOnClickListener {
            if (category.isNullOrEmpty() || category == "") {
                errorToast("Заполните все поля!", activity!!)
            }
            else
                initFirstIntent()
        }
        mImageViewSecondImage.setOnClickListener {
            initSecondIntent()
        }
        mImageViewThirdImage.setOnClickListener {
            initThirdIntent()
        }
        mImageViewFourthImage.setOnClickListener {
            initFourthIntent()
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
        if (requestCode == REQUEST_CODE_FOR_SINGLE_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                if (category == "Работа, Подработки") {
                    val data: Uri = data.data!!
                    mImageViewFirstImage.load(data)
                    val work_path =
                        FirebaseStorage.getInstance().reference.child(FOLDER_WORKS_IMAGE)
                            .child(UUID.randomUUID().toString())
                    work_path.putFile(data).addOnCompleteListener { task1 ->
                        if (task1.isSuccessful) {
                            work_path.downloadUrl.addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    firstImageURL = task2.result.toString()
                                }
                            }
                        }
                    }
                }

                if (category == "Транспорт, Перевозки") {
                    val data: Uri = data?.data!!
                    mImageViewFirstImage.load(data)
                    val transport_path = REF_STORAGE_ROOT.child(FOLDER_TRANSPORT_IMAGE)
                        .child(UUID.randomUUID().toString())
                    transport_path.putFile(data).addOnCompleteListener { task1 ->
                        if (task1.isSuccessful) {
                            transport_path.downloadUrl.addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    firstImageURL = task2.result.toString()
                                }
                            }
                        }
                    }
                }
                if (category == "Медицина, Красота") {
                    val data: Uri = data?.data!!
                    mImageViewFirstImage.load(data)
                    val beauty_path = REF_STORAGE_ROOT.child(
                        FOLDER_BEAUTY_AND_MEDICINE_IMAGES
                    ).child(UUID.randomUUID().toString())
                    mLinearLayout.visibility = View.VISIBLE
                    beauty_path.putFile(data).addOnCompleteListener { task1 ->
                        if (task1.isSuccessful) {
                            beauty_path.downloadUrl.addOnCompleteListener { task2 ->
                                if (task2.isSuccessful) {
                                    firstImageURL = task2.result.toString()
                                }
                            }
                        }
                    }
                }
            }
            if (category == "Продажа, Покупка") {
                val data: Uri = data?.data!!
                mImageViewFirstImage.load(data)
                val beauty_path = REF_STORAGE_ROOT.child(
                    FOLDER_BUY_SELL_IMAGES
                ).child(UUID.randomUUID().toString())
                mLinearLayout.visibility = View.VISIBLE
                beauty_path.putFile(data).addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        beauty_path.downloadUrl.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                firstImageURL = task2.result.toString()
                            }
                        }
                    }
                }
            }
            if (category == "Квартиры, Гостиницы") {
                val data: Uri = data?.data!!
                mImageViewFirstImage.load(data)
                val flats_path = REF_STORAGE_ROOT.child(
                    FOLDER_FLATS_IMAGES
                ).child(UUID.randomUUID().toString())
                mLinearLayout.visibility = View.VISIBLE
                flats_path.putFile(data).addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        flats_path.downloadUrl.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                firstImageURL = task2.result.toString()
                            }
                        }
                    }
                }
            }
            if (category == "Обучение, Услуги") {
                val data: Uri = data?.data!!
                mImageViewFirstImage.load(data)
                val flats_path = REF_STORAGE_ROOT.child(
                    FOLDER_SERVICES_IMAGES
                ).child(UUID.randomUUID().toString())
                mLinearLayout.visibility = View.VISIBLE
                flats_path.putFile(data).addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        flats_path.downloadUrl.addOnCompleteListener { task2 ->
                            if (task2.isSuccessful) {
                                firstImageURL = task2.result.toString()
                            }
                        }
                    }
                }
            }
        }
        if (requestCode == REQUEST_CODE_FOR_SECOND_IMAGES) {
            if (resultCode == RESULT_OK && data != null) {
                if (category == "Медицина, Красота") {
                    val data: Uri = data?.data!!
                    mImageViewSecondImage.load(data)
                    val beauty_path =
                        FirebaseStorage.getInstance().reference.child(
                            FOLDER_BEAUTY_AND_MEDICINE_IMAGES
                        ).child(UUID.randomUUID().toString())

                    beauty_path.putFile(data)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                beauty_path.downloadUrl.addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        secondImageURL = task2.result.toString()
                                    }
                                }
                            }
                        }
                }
                if (category == "Продажа, Покупка") {
                    val data: Uri = data?.data!!
                    mImageViewSecondImage.load(data)
                    val buys_sell__path =
                        FirebaseStorage.getInstance().reference.child(
                            FOLDER_BUY_SELL_IMAGES
                        ).child(UUID.randomUUID().toString())

                    buys_sell__path.putFile(data)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                buys_sell__path.downloadUrl.addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        secondImageURL = task2.result.toString()
                                    }
                                }
                            }
                        }
                }
            }
        }
        if (requestCode == REQUEST_CODE_FOR_THIRD_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                if (category == "Медицина, Красота") {
                    val data: Uri = data?.data!!
                    mImageViewThirdImage.load(data)
                    val beauty_path =
                        FirebaseStorage.getInstance().reference.child(
                            FOLDER_BEAUTY_AND_MEDICINE_IMAGES
                        ).child(UUID.randomUUID().toString())

                    beauty_path.putFile(data)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                beauty_path.downloadUrl.addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        thirdImageURL = task2.result.toString()
                                    }
                                }
                            }
                        }
                }
                if (category == "Продажа, Покупка") {
                    val data: Uri = data?.data!!
                    mImageViewThirdImage.load(data)
                    val buys_sell__path =
                        FirebaseStorage.getInstance().reference.child(
                            FOLDER_BUY_SELL_IMAGES
                        ).child(UUID.randomUUID().toString())

                    buys_sell__path.putFile(data)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                buys_sell__path.downloadUrl.addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        thirdImageURL = task2.result.toString()
                                    }
                                }
                            }
                        }
                }
            }
        }
        if (requestCode == REQUEST_CODE_FOR_FOURTH_IMAGE) {
            if (resultCode == RESULT_OK && data != null) {
                if (category == "Медицина, Красота") {
                    val data: Uri = data?.data!!
                    mImageViewFourthImage.load(data)
                    val beauty_path =
                        FirebaseStorage.getInstance().reference.child(
                            FOLDER_BEAUTY_AND_MEDICINE_IMAGES
                        ).child(UUID.randomUUID().toString())

                    beauty_path.putFile(data)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                beauty_path.downloadUrl.addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        fourthImageURL = task2.result.toString()
                                    }
                                }
                            }
                        }
                }
                if (category == "Продажа, Покупка") {
                    val data: Uri = data?.data!!
                    mImageViewFourthImage.load(data)
                    val buys_sell__path =
                        FirebaseStorage.getInstance().reference.child(
                            FOLDER_BUY_SELL_IMAGES
                        ).child(UUID.randomUUID().toString())

                    buys_sell__path.putFile(data)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                buys_sell__path.downloadUrl.addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        fourthImageURL = task2.result.toString()
                                    }
                                }
                            }
                        }
                }
            }
        }
    }


    fun setSelectedItem(selectedItem: String?) {
        val tv_metro: TextView = view!!.findViewById(R.id.tv_choose_metro)
        selected_station = selectedItem
        tv_metro.text = "    м. $selectedItem"
        mHandler = Handler()
        mRunnable = Runnable {
            dialog.dismiss()
        }
        mHandler!!.postDelayed(mRunnable!!, 500)
    }

    fun setSelectedCategory(selectedCategory: String?) {
        val tv_category: TextView =
            view!!.findViewById(R.id.tv_choose_category)
        category = selectedCategory
        tv_category.text = "    $category"
        mHandler = Handler()
        mRunnable = Runnable {
            dialog_category.dismiss()
        }
        mHandler!!.postDelayed(mRunnable!!, 500)
    }

    fun addToFB() {
        mRefAds = FirebaseDatabase.getInstance().reference
        val title = et_title.text.toString()
        val text = et_text.text.toString()
        val salary = et_salary.text.toString()
        val phone_number = et_phone_number.text.toString()
        uid = FirebaseAuth.getInstance().currentUser?.uid.toString()


        val work =
            Work(
                uid,
                title,
                text,
                salary,
                ServerValue.TIMESTAMP,
                selected_station,
                category.toString(), 0, 0,
                firstImageURL, phone_number
            )

        val beauty =
            Beauty(
                uid,
                title,
                text,
                salary,
                selected_station,
                ServerValue.TIMESTAMP,
                category.toString(), 0, 0,
                firstImageURL,
                secondImageURL,
                thirdImageURL,
                fourthImageURL, phone_number
            )
        val buySell = BuySell(
            uid,
            title,
            text,
            salary,
            selected_station,
            ServerValue.TIMESTAMP,
            category.toString(), 0, 0,
            firstImageURL,
            secondImageURL,
            thirdImageURL,
            fourthImageURL, phone_number
        )

        val transport = Transportation(
            uid,
            title,
            text,
            salary,
            selected_station,
            ServerValue.TIMESTAMP,
            category.toString(), 0, 0,
            firstImageURL, phone_number
        )

        val services = Services(
            uid,
            title,
            text,
            salary,
            selected_station,
            ServerValue.TIMESTAMP,
            category.toString(), 0, 0,
            firstImageURL, phone_number
        )

        val flats = Flats(
            uid,
            title,
            text,
            salary,
            ServerValue.TIMESTAMP,
            selected_station,
            category.toString(), 0, 0,
            firstImageURL, phone_number
        )

        if (category == "Медицина, Красота") {
            mRefAds.child(NODE_BEAUTY).child(Date().time.toString())
                .setValue(beauty)
        }
        if (category == "Транспорт, Перевозки") {
            mRefAds.child(NODE_TRANSPORT).child(Date().time.toString())
                .setValue(transport)
        }
        if (category == "Продажа, Покупка") {
            mRefAds.child(NODE_BUY_SELL).child(Date().time.toString())
                .setValue(buySell)
        }
        if (category == "Работа, Подработки") {
            mRefAds.child(NODE_WORKS).child(Date().time.toString())
                .setValue(work)
        }
        if (category == "Квартиры, Гостиницы") {
            mRefAds.child(NODE_HOUSE).child(Date().time.toString())
                .setValue(flats)
        }
        if (category == "Обучение, Услуги") {
            mRefAds.child(NODE_SERVICES).child(Date().time.toString())
                .setValue(services)
        }

    }


    fun initFirstIntent() {
        val intent_work: Intent = Intent(Intent.ACTION_PICK)
        intent_work.type = "image/*"
        startActivityForResult(
            intent_work,
            REQUEST_CODE_FOR_SINGLE_IMAGE
        )
    }

    fun initSecondIntent() {
        val intent_work: Intent = Intent(Intent.ACTION_PICK)
        intent_work.type = "image/*"
        startActivityForResult(
            intent_work,
            REQUEST_CODE_FOR_SECOND_IMAGES
        )
    }

    fun initThirdIntent() {
        val intent_work: Intent = Intent(Intent.ACTION_PICK)
        intent_work.type = "image/*"
        startActivityForResult(
            intent_work,
            REQUEST_CODE_FOR_THIRD_IMAGE
        )
    }

    fun initFourthIntent() {
        val intent_work: Intent = Intent(Intent.ACTION_PICK)
        intent_work.type = "image/*"
        startActivityForResult(
            intent_work,
            REQUEST_CODE_FOR_FOURTH_IMAGE
        )
    }

}


