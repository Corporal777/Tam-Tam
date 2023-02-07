package org.otunjargych.tamtam.ui.profile.items

import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemProfileUnauthorizedHeaderBinding
import org.otunjargych.tamtam.util.setUserImage

class ProfileHeaderUnauthorizedItem(private val town: String?) :
    BindableItem<ItemProfileUnauthorizedHeaderBinding>() {


    override fun bind(viewBinding: ItemProfileUnauthorizedHeaderBinding, position: Int) {
        viewBinding.apply {
            tvUserName.text = root.context.getString(R.string.user_unauthorized)
            ivUserPhoto.setUserImage(R.drawable.avatar_empty_square)
            tvCurrentTown.text = town ?: root.context.getString(R.string.not_chosen)
        }
    }

    override fun getLayout(): Int = R.layout.item_profile_unauthorized_header
}