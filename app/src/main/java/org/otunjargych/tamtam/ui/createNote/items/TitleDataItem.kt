package org.otunjargych.tamtam.ui.createNote.items

import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemCreateNoteDataTitleBinding

class TitleDataItem(private val title: String) : BindableItem<ItemCreateNoteDataTitleBinding>() {


    override fun bind(viewBinding: ItemCreateNoteDataTitleBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
        }
    }

    override fun getLayout(): Int = R.layout.item_create_note_data_title
}