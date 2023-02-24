package org.otunjargych.tamtam.ui.detailNote.items

import android.view.Gravity
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemNoteDetailImagesBinding
import org.otunjargych.tamtam.databinding.ItemNoteDetailMainInfoBinding
import org.otunjargych.tamtam.util.formatToDayMonth

class DetailMainInfoItem(
    private val name: String,
    private val salary: String,
    private val description: String?,
    private val likes: List<String>?,
    private val viewings: String
) : BindableItem<ItemNoteDetailMainInfoBinding>() {


    override fun bind(viewBinding: ItemNoteDetailMainInfoBinding, position: Int) {
        viewBinding.apply {
            tvName.text = name
            tvDescription.apply {
                if (!description.isNullOrEmpty()) text = description
                else {
                    gravity = Gravity.CENTER
                    text = root.context.getString(R.string.no_description)
                }
            }
            tvSalary.text = salary


            tvLikes.text = if (likes.isNullOrEmpty()) "0"
            else likes.size.toString()

            tvViewings.text = viewings
        }
    }

    override fun getLayout(): Int = R.layout.item_note_detail_main_info
}