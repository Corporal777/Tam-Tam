package org.otunjargych.tamtam.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.otunjargych.tamtam.model.Node

@Database(entities = [Node::class], version = 1)
@TypeConverters(NodesImagesConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun nodeDao(): NodeDao
}