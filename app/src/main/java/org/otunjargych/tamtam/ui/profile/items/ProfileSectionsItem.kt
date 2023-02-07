package org.otunjargych.tamtam.ui.profile.items

import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemProfileSectionsBinding
import org.otunjargych.tamtam.databinding.ItemProfileUnauthorizedHeaderBinding
import org.otunjargych.tamtam.ui.views.PasswordData
import org.otunjargych.tamtam.ui.views.PasswordView
import org.otunjargych.tamtam.util.setUserImage

class ProfileSectionsItem(isLoggedOut: Boolean) :
    BindableItem<ItemProfileSectionsBinding>() {

    private var onProfileSettingsClick: () -> Unit = {}
    private var onLogoutClick: () -> Unit = {}
    private var onShowLoginClick: () -> Unit = {}

    private lateinit var mBinding: ItemProfileSectionsBinding
    private var loggedOut = isLoggedOut

    override fun bind(viewBinding: ItemProfileSectionsBinding, position: Int) {
        viewBinding.apply {
            mBinding = this
            setLoggedOut(loggedOut)
            tvProfileSettings.setOnClickListener {
                onProfileSettingsClick.invoke()
            }
            tvLogout.setOnClickListener {
                if (loggedOut) onShowLoginClick.invoke()
                else onLogoutClick.invoke()
            }
        }
    }

    fun onProfileSettingsClick(block: () -> Unit): ProfileSectionsItem {
        onProfileSettingsClick = block
        return this
    }

    fun onLogoutClick(block: () -> Unit): ProfileSectionsItem {
        onLogoutClick = block
        return this
    }

    fun onShowLoginClick(block: () -> Unit): ProfileSectionsItem {
        onShowLoginClick = block
        return this
    }


    fun setLoggedOut(isLoggedOut: Boolean) {
        this.loggedOut = isLoggedOut
        if (this::mBinding.isInitialized) {
            mBinding.tvLogout.apply {
                text = if (isLoggedOut) context.getString(R.string.login_to_account)
                else context.getString(R.string.logout_from_account)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_profile_sections
}