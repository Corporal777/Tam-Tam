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
import org.otunjargych.tamtam.adapter.MyImagesAdapter
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.databinding.FragmentEditMyNodesBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.model.Node
import java.util.*
import kotlin.collections.ArrayList

class EditMyNodesFragment : BaseFragment() {

    private var listener: OnBottomAppBarStateChangeListener? = null
    private var _binding: FragmentEditMyNodesBinding? = null
    private val binding get() = _binding!!

    private lateinit var mNode: Node
    private lateinit var mAdapter: MyImagesAdapter
    private lateinit var mListener: MyImagesAdapter.OnImageDeleteListener
    private val mImagesList = ArrayList<String>()
    private lateinit var mListPopupWindow: ListPopupWindow
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null

    private var mDate: Long? = null
    private var mInvertedDate = ""
    private var mLikes = 0
    private var mViewings = 0
    private var mUserId = ""
    private var mUUId = ""
    private var mTitle = ""
    private var mText = ""
    private var mSalary = ""
    private var mPhoneNumber = ""
    private var mWhatsappNumber = ""
    private var mAddress = ""
    private var mStation: String = ""
    private var mCategory: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as OnBottomAppBarStateChangeListener
        } catch (e: Exception) {
            throw ClassCastException("Activity not attached!")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditMyNodesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.customTitle.text = "Редактировать"

        initRecyclerView()
        initFields()

        Boom(binding.btnSelectPhoto)
        binding.tvCategory.setOnClickListener {
            showCategoryList()
        }
        binding.tvMetro.setOnClickListener {
            showMetroStationsList()
        }
        binding.btnSelectPhoto.setOnClickListener {
            openImagePicker()
        }

        Boom(binding.btnAccept)
        binding.btnAccept.setOnClickListener {
            initNewParamsOfNode()
            mNode = Node(
                mUserId,
                mUUId,
                mTitle,
                mText,
                mSalary,
                mDate!!,
                mInvertedDate,
                mStation,
                mAddress,
                mCategory,
                mLikes,
                mViewings,
                mImagesList,
                mPhoneNumber,
                mWhatsappNumber
            )

            FireBaseHelper.editNodeFromFirestore(requireContext(), mCategory, mNode)
            mHandler = Handler()
            mRunnable = Runnable {
                requireActivity().supportFragmentManager.popBackStack()
            }
            mHandler!!.postDelayed(mRunnable!!, 1000)

        }
    }

    private fun initNewParamsOfNode() {
        mDate = mNode.timeStamp
        mLikes = mNode.likes
        mViewings = mNode.viewings
        mInvertedDate = mNode.invertedTimeStamp
        mUserId = mNode.userId
        mUUId = mNode.uuid
        mCategory = binding.tvCategory.text.toString()
        mStation = binding.tvMetro.text.toString()
        mText = binding.tvText.text.toString()
        mTitle = binding.tvTitle.text.toString()
        mSalary = binding.tvSalary.text.toString()
        mAddress = binding.tvAddress.text.toString()
        mPhoneNumber = binding.tvPhoneNumber.text.toString()
        mWhatsappNumber = binding.tvWhatsappNumber.text.toString()
    }

    private fun initFields() {
        val bundle: Bundle? = this.arguments
        if (bundle != null && !bundle.isEmpty) {
            mNode = bundle.getParcelable("note")!!

            binding.apply {
                tvCategory.text = mNode.category
                tvMetro.text = mNode.station
                tvTitle.setText(mNode.title)
                tvText.setText(mNode.text)
                tvPhoneNumber.setText(mNode.phone_number)
                tvWhatsappNumber.setText(mNode.whatsapp_number)
                tvSalary.setText(mNode.salary)
                tvAddress.setText(mNode.addres)
                if (!mNode.images.isNullOrEmpty()) {
                    mImagesList.addAll(mNode.images)
                    mAdapter.addImages(mImagesList, mListener)
                    listImages.adapter = mAdapter
                }
            }
        }
    }

    private fun showMetroStationsList() {
        val list = LocalSourceMetroStations().getMetroStationsList()
        mListPopupWindow = ListPopupWindow(requireContext())
        mListPopupWindow.apply {
            anchorView = binding.tvMetro
            width = 700
            height = 1200
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.station_item, R.id.tv_station, list
                )
            )
            isModal = true
            setOnItemClickListener { _, _, position, _ ->
                binding.tvMetro.text = list[position]
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
            width = 700
            height = 1200
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.category_item, R.id.tv_category, list
                )
            )
            isModal = true
            setOnItemClickListener { _, _, position, _ ->
                binding.tvCategory.text = list[position]
                dismiss()
            }
        }
        mListPopupWindow.show()

    }

    private fun initRecyclerView() {
        mListener = object : MyImagesAdapter.OnImageDeleteListener {
            override fun onImageDelete(image: String, position: Int) {
                mImagesList.removeAt(position)
            }
        }

        binding.listImages.layoutManager =
            GridLayoutManager(requireContext(), 2)
        mAdapter = MyImagesAdapter()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val images = data?.getParcelableArrayListExtra<Uri>(RESULT_NAME)
            images.let {
                if (it != null) {
                    addImage(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addImage(list: List<Uri>) {

        list.forEach {
            val path =
                REF_STORAGE_ROOT.child(
                    FOLDER_NOTES_IMAGES
                ).child(UUID.randomUUID().toString())
            path.putFile(it).addOnSuccessListener { task ->
                path.downloadUrl.addOnCompleteListener { status ->
                    if (status.isSuccessful) {
                        if (mImagesList.size < 6) {
                            mImagesList.add(status.result.toString())
                            mAdapter.addImage(status.result.toString())
                        }
                    }
                }
            }

            binding.listImages.adapter = mAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        listener?.onHide()
    }

    override fun onStop() {
        super.onStop()
        listener?.onShow()
    }
}