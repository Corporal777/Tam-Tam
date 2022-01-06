package org.otunjargych.tamtam.api

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.otunjargych.tamtam.extensions.NAME_PROPERTY
import org.otunjargych.tamtam.extensions.NOTES_COLLECTION
import org.otunjargych.tamtam.extensions.PAGE_SIZE
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideQueryProductsByName() = FirebaseFirestore.getInstance()
        .collection(NOTES_COLLECTION)
        .orderBy(NAME_PROPERTY, DESCENDING)
        .limit(PAGE_SIZE.toLong())

}