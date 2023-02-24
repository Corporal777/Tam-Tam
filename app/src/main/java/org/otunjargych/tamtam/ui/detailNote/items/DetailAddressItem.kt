package org.otunjargych.tamtam.ui.detailNote.items

import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemNoteDetailAddressBinding
import org.otunjargych.tamtam.databinding.ItemNoteDetailMainInfoBinding
import org.otunjargych.tamtam.util.formatToDayMonth

class DetailAddressItem(
    private val city: String?,
    private val region: String?,
    private val metro: List<String>?,
    private val date: String
) : BindableItem<ItemNoteDetailAddressBinding>() {

    override fun bind(viewBinding: ItemNoteDetailAddressBinding, position: Int) {
        viewBinding.apply {
            tvCity.text = if (city.isNullOrEmpty())
                root.context.getString(R.string.not_chosen)
            else city

            tvRegion.text =
                if (region.isNullOrEmpty()) root.context.getString(R.string.not_chosen)
                else region

            if (metro.isNullOrEmpty()) tvMetro.text = root.context.getString(R.string.not_chosen)
            else tvMetro.text = metro.joinToString("\n") { it }

            tvDate.text = date.formatToDayMonth()
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is DetailAddressItem) return false
        if (city != other.city) return false
        if (region != other.region) return false
        if (metro != other.metro) return false
        if (date != other.date) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note_detail_address

}