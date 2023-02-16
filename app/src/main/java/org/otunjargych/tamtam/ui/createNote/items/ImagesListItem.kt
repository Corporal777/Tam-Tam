package org.otunjargych.tamtam.ui.createNote.items

import android.graphics.Bitmap
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.ui.holders.GridListItem
import org.otunjargych.tamtam.ui.holders.NoteItem
import org.otunjargych.tamtam.ui.holders.PlaceholderItem

class ImagesListItem(
    val list: List<Bitmap>?,
    val onShowGalleryClick: () -> Unit,
    val onRemoveImageClick: (bitmap: Bitmap) -> Unit
) : GridListItem<GroupieViewHolder>() {

    private val addImageItem = GalleryImageItem(null,
        { onShowGalleryClick.invoke() }, { }
    )
    val items = arrayListOf<Item<*>>()

    private val imagesSection = Section()

    init {
        if (!list.isNullOrEmpty()) {
            imagesSection.update(
                list.map { image ->
                    GalleryImageItem(image, {}, {
                        onRemoveImageClick.invoke(it)
                    })
                }.plus(listOf(addImageItem))
            )
        } else imagesSection.add(addImageItem)

        adapter = GroupAdapter<GroupieViewHolder>().apply {
            add(imagesSection)
        }
    }

    fun update(list: List<Bitmap>) {
        imagesSection.update(
            list.map {
                GalleryImageItem(it, {}, { b ->
                    onRemoveImageClick.invoke(b)
                })
            }.plus(listOf(addImageItem))
        )
    }

}