package org.otunjargych.tamtam.ui.detailNote

import android.R.attr.factor
import android.R.color
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.navigation.fragment.navArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentNoteDetailBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.detailNote.data.NoteDetailData
import org.otunjargych.tamtam.ui.detailNote.items.DetailAddressItem
import org.otunjargych.tamtam.ui.detailNote.items.DetailImagesItem
import org.otunjargych.tamtam.ui.detailNote.items.DetailMainInfoItem
import org.otunjargych.tamtam.ui.detailNote.items.DetailWorkInfoItem
import org.otunjargych.tamtam.ui.views.LinearLayoutManagerAccurateOffset
import org.otunjargych.tamtam.util.onScrolled
import org.otunjargych.tamtam.util.updateItem
import kotlin.math.abs
import kotlin.reflect.KClass


class NoteDetailFragment : BaseFragment<NoteDetailViewModel, FragmentNoteDetailBinding>() {

    private val args: NoteDetailFragmentArgs by navArgs()

    private val imageSection = Section()
    private val mainInfoSection = Section()
    private val additionalInfoSection = Section()
    private val addressSection = Section()

    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(imageSection)
        add(mainInfoSection)
        add(additionalInfoSection)
        add(addressSection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadNoteDetail(args.noteId)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            noteDetailList.apply {
                layoutManager = LinearLayoutManagerAccurateOffset(requireContext())
                adapter = groupAdapter
                onScrolled { dx, dy ->
                    viewModel.changeScrollOffset(this.computeVerticalScrollOffset())
                }
            }
        }
        observeNoteDetail()
        updateAppBarBackgroundColorValue()
    }


    private fun observeNoteDetail() {
        viewModel.noteDetail.observe(viewLifecycleOwner) { note ->
            imageSection.updateItem(DetailImagesItem(note.field.images))
            mainInfoSection.updateItem(
                DetailMainInfoItem(
                    note.field.name,
                    note.field.getNoteSalary(),
                    note.field.description,
                    note.field.likes,
                    "0"
                )
            )
            if (note.value != null) {
                additionalInfoSection.updateItem(
                    when (note) {
                        is NoteDetailData.WorkData -> DetailWorkInfoItem(note.field, note.getData())
                    }
                )
            }
            addressSection.updateItem(
                DetailAddressItem(
                    note.field.address.city,
                    note.field.address.region?:"",
                    note.field.address.metro,
                    note.field.createdAt
                )
            )

        }
    }

    private fun updateAppBarBackgroundColorValue() {
        viewModel.scrollOffsetData.observe(viewLifecycleOwner) { offset ->
            Log.e("OFFSET", offset.toString())
            Log.e("X VALUE MASK", mBinding.menuMask.x.toString())
            Log.e("X VALUE LIKE", mBinding.ivMenu.x.toString())
            mBinding.apply {
                if (offset <= 0) {
                    tbBackground.alpha = 0f
                    backMask.alpha = 1f
                    likeMask.alpha = 1f
                    //ivBack.x = 42f
                    //backMask.x = 42f
                } else {
                    tbBackground.apply {
                        alpha = abs(offset / (700).toFloat())
                        backMask.alpha = 1f - abs(offset / (700).toFloat())
                        likeMask.alpha = 1f - abs(offset / (700).toFloat())
                        menuMask.alpha = 1f - abs(offset / (700).toFloat())
                    }
                    if (offset in 1..880) {
                        val xBack = 42f - abs(offset / (30).toFloat())
                        if (xBack in 10.2f..42.1f) {
                            ivBack.x = xBack
                            backMask.x = xBack
                        }

                        //menu
                        val xMenuMask = 781f + abs(offset / (30).toFloat())
                        if (xMenuMask in 781f..810.1f) likeMask.x = xMenuMask

                        val xMenu = 789f + abs(offset / (30).toFloat())
                        if (xMenu in 789f..830.1f) ivLike.x = xMenu

                        //like
                        val xLike = 930f + abs(offset / (30).toFloat())
                        if (xLike in 930f..960.1f) ivMenu.x = xLike

                        val xMask = 922f + abs(offset / (30).toFloat())
                        if (xMask in 922f..950.1f) menuMask.x = xMask
                    }
                }
            }
        }


    }

    override val layoutId: Int = R.layout.fragment_note_detail
    override fun getViewModelClass(): KClass<NoteDetailViewModel> = NoteDetailViewModel::class
}