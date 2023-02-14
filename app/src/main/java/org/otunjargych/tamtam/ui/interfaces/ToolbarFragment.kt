package org.otunjargych.tamtam.ui.interfaces

import android.graphics.drawable.Drawable
import android.view.ViewGroup

interface ToolbarFragment {
    val title: CharSequence
    fun toolbarIconsContainer(viewGroup: ViewGroup)
}