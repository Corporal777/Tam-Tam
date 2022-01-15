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
import androidx.recyclerview.widget.GridLayoutManager
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.ImagesAdapter
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.databinding.FragmentNewNodeBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.fragments.dialog_fragments.MyDialogFragment
import org.otunjargych.tamtam.model.Node
import java.util.*
import kotlin.collections.ArrayList


class NewNodeFragment : BaseFragment() {


    private lateinit var dialog: MyDialogFragment
    private lateinit var mListPopupWindow: ListPopupWindow
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null

    private var mImageList = ArrayList<Uri>()
    private var listener: OnBottomAppBarStateChangeListener? = null

    private var _binding: FragmentNewNodeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAdapter: ImagesAdapter

    private var title = ""
    private var text = ""
    private var salary = ""
    private var phoneNumber = ""
    private var whatsappNumber = ""
    private var address = ""
    private var mSelectedStation: String = ""
    private var mSelectedCategory: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewNodeBinding.inflate(inflater, container, false)
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

    private fun hideBottomAppBar() {
        listener?.onHide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRVListChosenImages()

        binding.apply {
            customTitle.text = "Объявление"
            toolbar.setNavigationOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
            tvCategory.setOnClickListener {
                showCategoryList()
            }
            tvMetro.setOnClickListener {
                showMetroStationsList()
            }
            Boom(btnSelectPhoto)
            btnSelectPhoto.setOnClickListener {
                openImagePicker()
            }
        }
        Boom(binding.btnAddNewAd)
        binding.btnAddNewAd.setOnClickListener {
            if (binding.tvCategory.text.isNullOrEmpty()) {
                binding.tvCategory.error = ""
                toastMessage(requireContext(), getString(R.string.empty_fields))
            }
            if (binding.tvText.text.isNullOrEmpty()) {
                binding.tvText.error = ""
                toastMessage(requireContext(), getString(R.string.empty_fields))
            }
            if (binding.tvTitle.text.isNullOrEmpty()) {
                binding.tvTitle.error = ""
                toastMessage(requireContext(), getString(R.string.empty_fields))
            } else {
                showSnackMessage()
            }

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
                FireBaseHelper.addImagesToStorage(mImageList)
            }
        }
    }

    private fun showMetroStationsList() {
        val list = LocalSourceMetroStations().getMetroStationsList()
        mListPopupWindow = ListPopupWindow(requireContext())
        mListPopupWindow.apply {
            anchorView = binding.tvMetro
            width = 640
            height = 800
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.station_item, R.id.tv_station, list
                )
            )
            isModal = true
            setOnItemClickListener { _, _, position, _ ->
                binding.tvMetro.text = list[position]
                mSelectedStation = list[position]
                dismiss()
            }
        }
        mListPopupWindow.show()
    }

    private fun showCategoryList() {
        val list = getCategoriesList()
        mListPopupWindow = ListPopupWindow(requireContext())
        mListPopupWindow.apply {
            anchorView = binding.tvCategory
            width = 640
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


    private fun addNewNodeData() {
        title = binding.tvTitle.text.toString()
        text = binding.tvText.text.toString()
        salary = binding.tvSalary.text.toString()
        phoneNumber = binding.tvPhoneNumber.text.toString()
        address = binding.tvAddress.text.toString()
        whatsappNumber = binding.tvWhatsappNumber.text.toString()

        val inverted = "-" + Date().time.toString()
        val uuid = UUID.randomUUID().toString()
        val list = ArrayList<String>()
        list.addAll(FireBaseHelper.getImagesUrlList())
        val time = Date().time
        val note = Node(
            USER_ID,
            uuid,
            title,
            text,
            salary,
            time,
            inverted,
            mSelectedStation,
            address,
            mSelectedCategory,
            0,
            0,
            list,
            phoneNumber,
            whatsappNumber
        )
        FireBaseHelper.addDataToFirestore(mSelectedCategory, note)
        //FireBaseHelper.addNewData(mSelectedCategory, note)
    }

    private fun initRVListChosenImages() {
        binding.listImages.layoutManager =
            GridLayoutManager(requireContext(), 2)
        mAdapter = ImagesAdapter()
    }

    private fun showSnackMessage() {
        snackMessage(requireContext(), view, getString(R.string.ad_was_pushed))
        mHandler = Handler()
        mRunnable = Runnable {
            addNewNodeData()
            //requireActivity().supportFragmentManager.popBackStack()
        }
        mHandler!!.postDelayed(mRunnable!!, 1500)

    }


    override fun onResume() {
        super.onResume()
        hideBottomAppBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






