package org.otunjargych.tamtam.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.activityViewModels
import coil.load
import com.google.android.material.tabs.TabLayoutMediator
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.ViewPagerAdapter
import org.otunjargych.tamtam.api.FireBaseHelper
import org.otunjargych.tamtam.databinding.FragmentDetailBinding
import org.otunjargych.tamtam.extensions.*
import org.otunjargych.tamtam.extensions.boom.Boom
import org.otunjargych.tamtam.model.Node
import org.otunjargych.tamtam.viewmodel.LikedNodesViewModel
import org.otunjargych.tamtam.viewmodel.UserViewModel
import java.util.*


class DetailFragment : BaseFragment() {

    private lateinit var adapter: ViewPagerAdapter
    private lateinit var category: String

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
    private var node = Node()

    private val mViewModel: UserViewModel by activityViewModels()
    private val mLikedNodesViewModel: LikedNodesViewModel by activityViewModels()

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
                    val bundle = Bundle()
                    bundle.putStringArrayList("images", imagesList)
                    val fragment = FullScreenImagesFragment()
                    fragment.arguments = bundle
                    replaceFragment(fragment)
                }
            }
        if (!imagesList.isNullOrEmpty()) {
            adapter.update(imagesList, onImageClick)
        }
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
            tvViewings.text = mDetailViewings
            tvLikes.text = mDetailLikes

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
                if (AUTH.currentUser != null && hasConnection(requireContext())) {
                    changeLikes()
                    mLikedNodesViewModel.insertNodesData(node)
                    btnLike.setImageResource(R.drawable.ic_liked)
                    btnLike.isClickable = false
                    val animScale: Animation =
                        AnimationUtils.loadAnimation(requireContext(), R.anim.scale)
                    btnLike.startAnimation(animScale)
                    toastMessage(requireContext(), "Добавлено в избранное")
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
        mViewModel.user.observe(viewLifecycleOwner, { user ->
            if (!user.name.isNullOrEmpty() || !user.last_name.isNullOrEmpty()) {
                binding.tvDetailUserName.text = user.name + " " + user.last_name
            } else binding.tvDetailUserName.text =
                getString(R.string.not_accepted)

            if (!user.image.isEmpty()) {
                binding.ivDetailUserPhoto.load(user.image)
            } else binding.ivDetailUserPhoto.load(R.drawable.empty_avatar)
        })

    }

    override fun onResume() {
        super.onResume()
        changeViewings()
        if (hasConnection(requireContext())) {
            mLikedNodesViewModel.checkLikedNode()
            mViewModel.loadUserData(mDetailUserId)
            if (AUTH.currentUser != null) {
                checkLikedNodes(node)
            }
        } else {
            toastMessage(requireContext(), getString(R.string.no_connection))
        }

    }

    override fun onPause() {
        super.onPause()
    }


    private fun changeLikes() {
        var like = node.likes
        like++
        FireBaseHelper.changeLikeNumber(mDetailCategory, mDetailId, like)
    }


    private fun changeViewings() {
        var viewing = node.viewings
        viewing++
        FireBaseHelper.changeViewingNumber(node.category, node.uuid, viewing)
    }

    private fun checkLikedNodes(node: Node) {
        mLikedNodesViewModel.check.observe(viewLifecycleOwner, {
            it.forEach { liked ->
                if (node.uuid.contentEquals(liked.uuid)) {
                    binding.btnLike.setImageResource(R.drawable.ic_liked)
                    binding.btnLike.isClickable = false
                }
            }

        })
    }

    private fun getFields() {
        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            node = bundle.getParcelable("note")!!
            if (node != null) {
                mDetailTitle = node.title
                mDetailText = node.text
                mDetailCategory = node.category
                mDetailSalary = node.salary
                mDetailStation = node.station
                mDetailDate = node.timeStamp.toString()
                mDetailPhone = node.phone_number
                mDetailWhatsapp = node.whatsapp_number
                mDetailLikes = node.likes.toString()
                mDetailViewings = node.viewings.toString()
                mDetailId = node.uuid
                mDetailAddress = node.addres
                mDetailUserId = node.userId
                mDetailViewings = node.viewings.toString()
                mDetailLikes = node.likes.toString()

                imagesList.clear()
                if (!node.images.isNullOrEmpty()) {
                    imagesList.addAll(node.images)
                } else {
                    imagesList.add(getEmptyImage())
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