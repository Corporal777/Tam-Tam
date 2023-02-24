package org.otunjargych.tamtam.ui.detailNote.items

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemNoteDetailImageBinding
import org.otunjargych.tamtam.databinding.ItemNoteDetailImagesBinding

class DetailImageItem(
    private val image: String
) : BindableItem<ItemNoteDetailImageBinding>() {


    override fun bind(viewBinding: ItemNoteDetailImageBinding, position: Int) {
        viewBinding.apply {
            noteDetailImage.apply {
                Glide
                    .with(context)
                    .load(image)
                    .placeholder(R.drawable.background_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(this)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_note_detail_image
}