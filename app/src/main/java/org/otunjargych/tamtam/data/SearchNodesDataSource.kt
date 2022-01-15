package org.otunjargych.tamtam.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import org.otunjargych.tamtam.extensions.isSimilar
import org.otunjargych.tamtam.model.Node

class SearchNodesDataSource(private val search: String, private val queryNotes: Query) :
    PagingSource<QuerySnapshot, Node>() {


    override fun getRefreshKey(state: PagingState<QuerySnapshot, Node>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Node> {
        return try {
            val currentPage = params.key ?: queryNotes.get().await()
            val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
            val nextPage = queryNotes.startAfter(lastVisibleProduct).get().await()

            val list = ArrayList<Node>()
            for (document in currentPage.documents) {
                val node = document.toObject(Node::class.java)
                if (node != null) {
                    if (isSimilar(node, search))
                        list.add(node)
                }
            }
            LoadResult.Page(
                list,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}