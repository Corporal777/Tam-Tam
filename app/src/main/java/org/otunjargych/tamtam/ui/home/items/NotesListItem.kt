package org.otunjargych.tamtam.ui.home.items

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.ui.holders.NoteItem
import org.otunjargych.tamtam.ui.holders.GridListItem
import org.otunjargych.tamtam.ui.holders.PlaceholderItem

class NotesListItem(val list: List<NoteModel?>) : GridListItem<GroupieViewHolder>() {


    val items = list.map { note ->
        if (note == null) PlaceholderItem(PlaceholderItem.Type.NOTE)
        else NoteItem(note)
    }

    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(items)
        }
    }

}