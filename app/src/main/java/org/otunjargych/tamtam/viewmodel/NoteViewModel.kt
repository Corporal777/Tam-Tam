package org.otunjargych.tamtam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.data.NotesDataSource
import org.otunjargych.tamtam.model.Note
import javax.inject.Inject

@HiltViewModel
class NoteViewModel
@Inject constructor(private val api: ApiService) : ViewModel() {


    val listData =
        Pager(getDefaultPageConfig()) {
            NotesDataSource(api)

        }.flow

    fun load(): Flow<PagingData<Note>> {
        return listData
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = 5, enablePlaceholders = true)
    }
}