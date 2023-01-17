package org.otunjargych.tamtam.data.room

import androidx.room.*
import org.otunjargych.tamtam.model.Node

@Dao
interface NodeDao {
    @Query("SELECT * FROM node")
    suspend fun getLikedNodes(): List<Node>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: Node)

    @Delete
    suspend fun deleteNode(node: Node)
}