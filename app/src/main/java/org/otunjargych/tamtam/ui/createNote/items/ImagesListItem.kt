package org.otunjargych.tamtam.ui.createNote.items

import android.graphics.Bitmap
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.ui.holders.GridListItem
import org.otunjargych.tamtam.ui.holders.NoteItem
import org.otunjargych.tamtam.ui.holders.PlaceholderItem

class ImagesListItem(
    val list: List<Bitmap>?,
    val onShowGalleryClick: () -> Unit
) : GridListItem<GroupieViewHolder>() {

    private val addImageItem = GalleryImageItem(null) {
        onShowGalleryClick.invoke()
    }
    val items = arrayListOf<Item<*>>()

    init {
        if (!list.isNullOrEmpty()) {
            list.forEach { image ->
                items.add(GalleryImageItem(image) {})
            }
        }
        items.add(addImageItem)
        adapter = GroupAdapter<GroupieViewHolder>().apply {
            update(items)
        }
    }

    fun update(list: List<Bitmap>) {
        items.clear()
        items.addAll(list.map { GalleryImageItem(it) {} })
        items.add(addImageItem)
        (adapter as GroupAdapter).apply {
            update(items)
        }
    }

}