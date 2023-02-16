package org.otunjargych.tamtam.ui.holders

import android.content.Context
import android.widget.TextView
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemNoteBinding
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.util.*

class NoteItem(private val note: NoteModel) : BindableItem<ItemNoteBinding>() {


    override fun bind(viewBinding: ItemNoteBinding, position: Int) {
        viewBinding.apply {
            ivNoteImage.loadNoteImage(note.images?.get(0))
            tvCategory.text = note.getCategory(root.context)

            tvSalary.text =
                if (note.salary.isNullOrEmpty()) root.context.getString(R.string.not_chosen) else note.salary

            tvDescription.apply {
                text = if (note.description.isNullOrEmpty()) note.name.replace("\n", " ")
                else note.description.replace("\n", " ")
            }
            tvLocation.text = note.getLocation()
            tvDate.text = note.createdAt.formatToDayMonth()
        }
    }

    override fun getLayout(): Int = R.layout.item_note
}