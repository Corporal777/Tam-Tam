package org.otunjargych.tamtam.di.repo.story

import io.reactivex.Maybe
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.model.request.StoriesModel
import org.otunjargych.tamtam.model.request.StoryModel

class StoryRepositoryImpl(private val apiService: ApiService) : StoryRepository {
    override fun getStoriesList(): Maybe<List<StoriesModel>> = apiService.getStoriesList()
}