package org.otunjargych.tamtam.ui.holders

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemStoryBinding
import org.otunjargych.tamtam.model.request.StoriesModel

class StoryItem(
    private val story: StoriesModel,
    private val onStoryClick: (story: StoriesModel) -> Unit
) : BindableItem<ItemStoryBinding>() {


    override fun bind(viewBinding: ItemStoryBinding, position: Int) {
        viewBinding.apply {
            ivImage.apply {
                Glide
                    .with(context)
                    .load(story.logo)
                    .optionalCenterCrop()
                    .placeholder(R.drawable.background_image_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(this)
            }
            tvTitle.text = story.title
            clStory.setOnClickListener {
                onStoryClick.invoke(story)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_story
}