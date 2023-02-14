package org.otunjargych.tamtam.ui.createNote.items

import android.graphics.Bitmap
import androidx.core.view.isVisible
import com.bumptech.glide.request.RequestOptions
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemImageGalleryBinding
import org.otunjargych.tamtam.util.loadImage
import org.otunjargych.tamtam.util.setImage


class GalleryImageItem(
    private val bitmap: Bitmap?,
    private val onShowGalleryClick: () -> Unit
) : BindableItem<ItemImageGalleryBinding>() {


    override fun bind(viewBinding: ItemImageGalleryBinding, position: Int) {
        viewBinding.apply {
            lnNoImage.isVisible = bitmap == null
            ivImage.apply {
                isVisible = bitmap != null
                loadImage(bitmap)
            }
            clImage.setOnClickListener {
                onShowGalleryClick.invoke()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_image_gallery
}