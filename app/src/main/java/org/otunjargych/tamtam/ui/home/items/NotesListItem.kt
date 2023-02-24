package org.otunjargych.tamtam.ui.home.items

import android.util.Log
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.ui.holders.NoteItem
import org.otunjargych.tamtam.ui.holders.GridListItem
import org.otunjargych.tamtam.ui.holders.OnNoteActionsListener
import org.otunjargych.tamtam.ui.holders.PlaceholderItem
import org.otunjargych.tamtam.util.findItemBy

class NotesListItem(
    val list: List<NoteModel?>,
    val listener: OnNoteActionsListener,
    private val itemId: Long = -100L
) : GridListItem<GroupieViewHolder>(itemId) {


    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply {
            update(list.map { note ->
                if (note == null) PlaceholderItem(PlaceholderItem.Type.NOTE)
                else NoteItem(note, listener)
            })
            //addAll(items)
        }
    }

    fun updateItems(list: List<NoteModel?>) {
        (adapter as GroupAdapter<GroupieViewHolder>).apply {
            update(list.map { note ->
                if (note != null) NoteItem(note, listener)
                else null
            })
        }
    }

    fun updateItem(updated: NoteModel) {
        (adapter as GroupAdapter<GroupieViewHolder>).findItemBy { item: NoteItem -> item.id == updated.id.toLong() }
            ?.notifyChanged(updated)
    }

}