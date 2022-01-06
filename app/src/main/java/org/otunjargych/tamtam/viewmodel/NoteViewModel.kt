package org.otunjargych.tamtam.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import org.otunjargych.tamtam.data.NotesDataSource
import org.otunjargych.tamtam.extensions.PAGE_SIZE
import org.otunjargych.tamtam.model.Note
import javax.inject.Inject

@HiltViewModel
class NoteViewModel
@Inject constructor(private val queryNotes: Query) : ViewModel() {


    private val listData =
        Pager(getDefaultPageConfig()) {
            NotesDataSource(queryNotes)

        }.flow.cachedIn(viewModelScope)

    fun load(): Flow<PagingData<Note>> {
        Log.e("Error", listData.toString())
        return listData
    }

    private fun getDefaultPageConfig(): PagingConfig {
        return PagingConfig(pageSize = PAGE_SIZE)
    }
}