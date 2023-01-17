package org.otunjargych.tamtam.data.room

import org.otunjargych.tamtam.model.Node

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper{
    override suspend fun getNodes(): List<Node> {
    return appDatabase.nodeDao().getLikedNodes()
    }

    override suspend fun insertNode(node: Node) {
    return appDatabase.nodeDao().insertNode(node)
    }

    override suspend fun deleteNode(node: Node) {
        return appDatabase.nodeDao().deleteNode(node)
    }
}