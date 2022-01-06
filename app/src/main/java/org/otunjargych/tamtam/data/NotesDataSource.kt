package org.otunjargych.tamtam.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.model.Note

class NotesDataSource(private val api: ApiService) : PagingSource<Int, Note>() {
    override fun getRefreshKey(state: PagingState<Int, Note>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Note> {
        return try {
            val limit = params.key ?: 5
            val response = api.loadWorkNotes("\"text\"", limit)

            val data = response.body()

            val responseData = ArrayList<Note>()
            if (data != null) {
                responseData.add(data)
            }
            LoadResult.Page(
                responseData,
                if (limit == 5) null else -1,
                limit.plus(1)
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}