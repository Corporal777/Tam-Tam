package org.otunjargych.tamtam.ui.detailNote.items

import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemNoteDetailImagesBinding

class DetailImagesItem(private val listImages: List<String>?) :
    BindableItem<ItemNoteDetailImagesBinding>() {

    private val imagesSection = Section()
    private val groupAdapter = GroupAdapter<GroupieViewHolder>().apply {
        add(imagesSection)
    }


    init {
        imagesSection.apply {
            if (!listImages.isNullOrEmpty()){
                update(listImages.map {
                    DetailImageItem(it)
                })
            }
        }
    }

    override fun bind(viewBinding: ItemNoteDetailImagesBinding, position: Int) {
        viewBinding.apply {
            viewPagerImages.apply {
                adapter = groupAdapter
                TabLayoutMediator(tabDots, this) { tab, position ->
                }.attach()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_note_detail_images
}