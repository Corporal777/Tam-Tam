package org.otunjargych.tamtam.ui.home.items

import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemTitleHomeBinding

class TitleItemHome(private val title : String) : BindableItem<ItemTitleHomeBinding>() {

    override fun bind(viewBinding: ItemTitleHomeBinding, position: Int) {
        viewBinding.apply {
            if (!title.isNullOrEmpty()){
                tvTitle.text = title
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_title_home
}