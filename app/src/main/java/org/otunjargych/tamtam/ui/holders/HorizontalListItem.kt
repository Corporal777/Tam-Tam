package org.otunjargych.tamtam.ui.holders

import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemGridListBinding
import org.otunjargych.tamtam.databinding.ItemHorizontalListBinding

open class HorizontalListItem<VH : RecyclerView.ViewHolder> : BindableItem<ItemHorizontalListBinding>() {

    var adapter: RecyclerView.Adapter<VH>? = null

    override fun bind(viewBinding: ItemHorizontalListBinding, position: Int) {
        viewBinding.recyclerView.apply {
            adapter = this@HorizontalListItem.adapter
        }
    }

    override fun getLayout() = R.layout.item_horizontal_list
}