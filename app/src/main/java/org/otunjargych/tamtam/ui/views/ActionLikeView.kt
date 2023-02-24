package org.otunjargych.tamtam.ui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.util.dp

class ActionLikeView : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val likeView = AppCompatImageView(context)
    private var actionType = ActionType.LIKE
    var onLikeClickListener: OnLikeClickListener? = null

    init {
        background = ContextCompat.getDrawable(context, R.drawable.circle_alpha_background)
        foreground = ContextCompat.getDrawable(context, R.drawable.custom_btn_toolbar_selectable)
        addView(likeView.apply {
            layoutParams = LinearLayout.LayoutParams(20.dp, 20.dp)
            setPadding(0, 2.dp, 0, 0)
            setImageResource(R.drawable.ic_like_empty)
            imageTintList = ContextCompat.getColorStateList(context, R.color.app_main_color)
        })
        setOnClickListener {
            if (actionType != ActionType.NONE) onLikeClickListener?.invoke(actionType)
        }
    }

    fun setLike() {
        likeView.imageTintList = ContextCompat.getColorStateList(context, R.color.liked_heart_text_color)
        likeView.setImageResource(R.drawable.ic_like_fill)
        actionType = ActionType.DISLIKE
    }

    fun setDislike() {
        likeView.imageTintList = ContextCompat.getColorStateList(context, R.color.app_main_color)
        likeView.setImageResource(R.drawable.ic_like_empty)
        actionType = ActionType.LIKE
    }

    fun setEdit() {
        actionType = ActionType.EDIT
    }

    fun setNone() {
        this.isInvisible = true
        actionType = ActionType.NONE
    }

    enum class ActionType {
        LIKE, DISLIKE, EDIT, NONE
    }
}

typealias OnLikeClickListener = (likeType: ActionLikeView.ActionType) -> Unit