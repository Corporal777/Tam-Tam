package org.otunjargych.tamtam.data.room

import org.otunjargych.tamtam.model.Node

interface DatabaseHelper {
    suspend fun getNodes(): List<Node>
    suspend fun insertNode(node: Node)
    suspend fun deleteNode(node: Node)
}