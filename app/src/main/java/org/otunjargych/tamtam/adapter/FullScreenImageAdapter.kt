package org.otunjargych.tamtam.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import org.otunjargych.tamtam.extensions.TouchImageView

class FullScreenImageAdapter(private val photoList: MutableList<String>) :
    RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return photoList.size
    }

    class ViewHolder(view: TouchImageView) : RecyclerView.ViewHolder(view) {
        val imagePlace = view
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TouchImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setOnTouchListener { view, event ->
                var result = true
                if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && canScrollHorizontally(
                        -1
                    )
                ) {
                    result = when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            parent.requestDisallowInterceptTouchEvent(true)
                            false
                        }
                        MotionEvent.ACTION_UP -> {
                            parent.requestDisallowInterceptTouchEvent(false)
                            true
                        }
                        else -> true
                    }
                }
                result
            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(holder.itemView.context)
            .load(photoList[position])
            .into(object : CustomTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable?>?
                ) {
                    holder.imagePlace.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit
            })
    }

    override fun getItemViewType(i: Int): Int {
        return 0
    }

}