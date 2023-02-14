package org.otunjargych.tamtam.ui.stories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.otunjargych.tamtam.model.request.StoriesModel
import org.otunjargych.tamtam.model.request.StoryModel
import org.otunjargych.tamtam.model.request.StoryPhoneModel
import org.otunjargych.tamtam.ui.base.BaseViewModel

class StoriesViewModel : BaseViewModel() {

    private var position = 0
    private val storiesList = arrayListOf<StoryModel>()
    private val _story = MutableLiveData<StoryModel>()
    val story : LiveData<StoryModel> get() = _story


    fun setStories(storiesModel : StoriesModel){
        storiesList.addAll(storiesModel.stories)
        _story.value = storiesList[position]
    }

    fun onNextStory() {
        if (position < storiesList.size - 1){
            position += 1
            _story.value = storiesList[position]
        }
    }

    fun onPrevStory() {
        if (position > 0){
            position -= 1
            _story.value = storiesList[position]
        }
    }

    fun getStoriesCount() = storiesList.size
}