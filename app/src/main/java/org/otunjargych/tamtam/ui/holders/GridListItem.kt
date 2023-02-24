package org.otunjargych.tamtam.ui.holders

import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemGridListBinding

open class GridListItem<VH : RecyclerView.ViewHolder>(id : Long) : BindableItem<ItemGridListBinding>(id) {

    var adapter: RecyclerView.Adapter<VH>? = null

    override fun bind(viewBinding: ItemGridListBinding, position: Int) {
        viewBinding.recyclerView.apply {
            adapter = this@GridListItem.adapter
        }
    }

    override fun getLayout() = R.layout.item_grid_list
}