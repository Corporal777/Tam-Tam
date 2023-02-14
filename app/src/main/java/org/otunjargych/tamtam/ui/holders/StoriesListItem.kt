package org.otunjargych.tamtam.ui.holders

import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import org.otunjargych.tamtam.model.request.StoriesModel

class StoriesListItem(
    val list: List<StoriesModel>,
    private val onStoryClick: (story: StoriesModel) -> Unit
) : HorizontalListItem<GroupieViewHolder>() {

    val items = list.map { story ->
        StoryItem(story) {
            onStoryClick(story)
        }
    }

    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply {
            update(items)
        }
    }

}