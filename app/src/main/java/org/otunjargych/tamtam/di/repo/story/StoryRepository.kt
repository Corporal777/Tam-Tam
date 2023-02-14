package org.otunjargych.tamtam.di.repo.story

import io.reactivex.Maybe
import org.otunjargych.tamtam.model.request.StoriesModel
import org.otunjargych.tamtam.model.request.StoryModel

interface StoryRepository {
    fun getStoriesList() : Maybe<List<StoriesModel>>
}