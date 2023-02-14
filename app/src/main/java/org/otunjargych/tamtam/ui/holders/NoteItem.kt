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
            ivNoteImage.setNoteImage(note.images?.get(0))
            tvCategory.text = getCategory(root.context)

            tvSalary.text =
                if (note.salary.isNullOrEmpty()) root.context.getString(R.string.not_chosen) else note.salary

            tvDescription.apply {
                text = if (note.description.isNullOrEmpty()) note.name.replace("\n", " ")
                else note.description.replace("\n", " ")
            }
            tvLocation.text = getLocation()
            tvDate.text = note.createdAt.formatToDayMonth()
        }
    }


    private fun getCategory(context: Context): String {
        return when (note.category) {
            "work" -> context.getString(R.string.work_category)
            else -> context.getString(R.string.not_chosen)
        }
    }

    private fun getLocation(): String {
        var location = note.address.city
        if (!note.address.metro.isNullOrEmpty()) {
            location = note.address.city + ", " + note.address.metro.first()
        }
        return location
    }


    override fun getLayout(): Int = R.layout.item_note
}