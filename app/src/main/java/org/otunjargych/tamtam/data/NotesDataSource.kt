package org.otunjargych.tamtam.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.model.Note

class NotesDataSource(private val queryNotes: Query) : PagingSource<QuerySnapshot, Note>() {


    override fun getRefreshKey(state: PagingState<QuerySnapshot, Note>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Note> {
        return try {
            val currentPage = params.key ?: queryNotes.get().await()
            val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
            val nextPage = queryNotes.startAfter(lastVisibleProduct).get().await()

            LoadResult.Page(
                data = currentPage.toObjects(Note::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}