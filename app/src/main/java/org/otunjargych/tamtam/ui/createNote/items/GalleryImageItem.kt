package org.otunjargych.tamtam.ui.createNote.items

import android.graphics.Bitmap
import androidx.core.view.isVisible
import com.bumptech.glide.request.RequestOptions
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemImageGalleryBinding
import org.otunjargych.tamtam.util.loadImage
import org.otunjargych.tamtam.util.setImage


class GalleryImageItem(
    private val bitmap: Bitmap?,
    private val onShowGalleryClick: () -> Unit,
    private val onDeleteImageClick: (bitmap : Bitmap) -> Unit,
) : BindableItem<ItemImageGalleryBinding>() {


    override fun bind(viewBinding: ItemImageGalleryBinding, position: Int) {
        viewBinding.apply {
            lnNoImage.isVisible = bitmap == null
            ivImage.apply {
                isVisible = bitmap != null
                loadImage(bitmap)
            }
            ivClose.apply {
                isVisible = bitmap != null
                setOnClickListener {
                bitmap?.let { b -> onDeleteImageClick.invoke(b) } }
            }
            clImage.setOnClickListener {
                onShowGalleryClick.invoke()
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is GalleryImageItem) return false
        //if (bitmap != other.bitmap) return false
        if (bitmap?.sameAs(other.bitmap) == false) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_image_gallery
}