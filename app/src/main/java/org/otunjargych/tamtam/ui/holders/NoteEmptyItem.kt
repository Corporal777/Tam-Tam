package org.otunjargych.tamtam.ui.holders

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemNoteEmptyBinding
import org.otunjargych.tamtam.databinding.ItemStoryBinding
import org.otunjargych.tamtam.model.request.StoriesModel

class NoteEmptyItem(
    private val title: String
) : BindableItem<ItemNoteEmptyBinding>() {


    override fun bind(viewBinding: ItemNoteEmptyBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is NoteEmptyItem) return false
        if (title != other.title) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note_empty
}