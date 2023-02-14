package org.otunjargych.tamtam.ui.holders

import androidx.core.view.isVisible
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemMainButtonBinding

class MainButtonItem(
    private val withDivider: Boolean = true,
    private val buttonText: String,
   private val onButtonClick: () -> Unit
) : BindableItem<ItemMainButtonBinding>() {



    override fun bind(viewBinding: ItemMainButtonBinding, position: Int) {
        viewBinding.apply {
            divider.isVisible = withDivider
            btnSave.apply {
                text = buttonText
                setOnClickListener {
                    onButtonClick.invoke()
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_main_button
}