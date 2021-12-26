package org.otunjargych.tamtam.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.ImagesAdapter
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.databinding.FragmentNewAdBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.imagepicker.ui.ImagePickerView
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment
import org.otunjargych.tamtam.model.Note
import java.util.*
import kotlin.collections.ArrayList


class NewAdFragment : BaseFragment() {


    private lateinit var dialog: MyDialogFragment
    private lateinit var mListPopupWindow: ListPopupWindow
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null

    private var mImageList = ArrayList<Uri>()

    private lateinit var userId: String

    private var listener: OnBottomAppBarStateChangeListener? = null

    private var _binding: FragmentNewAdBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdapter: ImagesAdapter

    private var title = ""
    private var text = ""
    private var salary = ""
    private var phoneNumber = ""
    private var whatsappNumber = ""
    private var address = ""
    private var selectedStation: String? = ""
    private var mSelectedCategory: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewAdBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as OnBottomAppBarStateChangeListener
        } catch (e: Exception) {
            throw ClassCastException("Activity not attached!")
        }
    }

    private fun changeStateBottomAppBar() {
        listener?.onHide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStateBottomAppBar()
        initRVListChosenImages()
        binding.run {
            toolbar.setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            customTitle.text = "Объявление"
            tvCategory.setOnClickListener {
                showCategoryList()
            }
            tvMetro.setOnClickListener {

            }
            layoutChoosePhoto.btnSelectPhoto.setOnClickListener {
                openImagePicker()
            }

        }


        binding.btnAddNewAd.setOnClickListener {
            FireBaseHelper.addImagesToStorage(mImageList)

            showSnackMessage()


//            Toast.makeText(
//                requireContext(),
//                FireBaseHelper.getImagesUrlList().size.toString(),
//                Toast.LENGTH_LONG
//            ).show()
//            if (binding.tvCategory.text.isNullOrEmpty()) {
//                binding.tvCategory.error = ""
//                toastMessage(requireContext(), getString(R.string.empty_fields))
//            }
//            if (binding.tvText.text.isNullOrEmpty()) {
//                binding.tvText.error = ""
//                toastMessage(requireContext(), getString(R.string.empty_fields))
//            }
//            if (binding.tvTitle.text.isNullOrEmpty()) {
//                binding.tvTitle.error = ""
//                toastMessage(requireContext(), getString(R.string.empty_fields))
//            }
//            if (binding.tvMetro.text.isNullOrEmpty()) {
//                binding.tvMetro.error = ""
//                toastMessage(requireContext(), getString(R.string.empty_fields))
//            } else {
//                //addNoteDataToFB()
//                showSnackMessage()
//            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            mImageList.clear()
            val images = data?.getParcelableArrayListExtra<Uri>(RESULT_NAME)
            images?.let {
                mImageList.addAll(it)
                mAdapter.update(mImageList)
                binding.listImages.adapter = mAdapter

            }
        }
    }

    private fun showCategoryList() {
        val list = getCategoriesList()
        mListPopupWindow = ListPopupWindow(requireContext())
        mListPopupWindow.apply {
            anchorView = binding.tvCategory
            width = 600
            height = 500
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.category_item, R.id.tv_category, list
                )
            )
            isModal = true
            setOnItemClickListener { _, _, position, _ ->
                binding.tvCategory.text = list[position]
                mSelectedCategory = list[position]
                dismiss()
            }
        }
        mListPopupWindow.show()

    }

    fun setSelectedItem(selectedItem: String?) {
        val tvMetro: TextView = view!!.findViewById(R.id.tv_metro)
        selectedStation = selectedItem
        tvMetro.text = "    м. $selectedItem"
        mHandler = Handler()
        mRunnable = Runnable {
            dialog.dismiss()
        }
        mHandler!!.postDelayed(mRunnable!!, 500)
    }

    private fun addNoteDataToFB() {

        title = binding.tvTitle.text.toString()
        text = binding.tvText.text.toString()
        salary = binding.tvSalary.text.toString()
        phoneNumber = binding.tvPhoneNumber.text.toString()
        address = binding.tvAddress.text.toString()
        whatsappNumber = binding.tvWhatsappNumber.text.toString()

        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val uuid = UUID.randomUUID().toString()
        val list = ArrayList<String>()
        list.addAll(FireBaseHelper.getImagesUrlList())

        val time = Date().time

        val note = Note(
            userId,
            uuid,
            title,
            text,
            salary,
            time,
            selectedStation!!,
            address,
            mSelectedCategory!!,
            0,
            0,
            list,
            phoneNumber,
            whatsappNumber
        )

        FireBaseHelper.addNewData(mSelectedCategory!!, note)


    }

    private fun initRVListChosenImages() {
        binding.listImages.setHasFixedSize(true)
        binding.listImages.layoutManager =
            GridLayoutManager(requireContext(), 2)
        mAdapter = ImagesAdapter()
    }

    private fun openImagePicker() {
        ImagePickerView.Builder()
            .setup {
                name { RESULT_NAME }
                max { 6 }
                title { "Галлерея" }
                single { false }
            }
            .start(this@NewAdFragment)
    }

    private fun showSnackMessage() {
//        snackMessage(requireContext(), view, getString(R.string.ad_was_publicated))
        mHandler = Handler()
        mRunnable = Runnable {
            addNoteDataToFB()
            requireActivity().supportFragmentManager.popBackStack()
        }
        mHandler!!.postDelayed(mRunnable!!, 1500)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






