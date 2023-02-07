package org.otunjargych.tamtam.ui.holders

import android.content.Context
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemNoteBinding
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.util.formatToDayMonth
import org.otunjargych.tamtam.util.setImage

class NoteItem(private val note: NoteModel) : BindableItem<ItemNoteBinding>() {


    override fun bind(viewBinding: ItemNoteBinding, position: Int) {
        viewBinding.apply {
            ivNoteImage.setImage(note.images?.get(0))
            tvCategory.text = getCategory(root.context)
            tvSalary.text = note.salary ?: root.context.getString(R.string.not_chosen)
            tvDescription.apply {
                text = if (note.description.isNullOrEmpty()) note.name
                else note.description
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
        if (!note.address.metro.isNullOrEmpty()){
            location = note.address.city + ", " + note.address.metro.first()
        }
        return location
    }

    override fun getLayout(): Int = R.layout.item_note
}