package org.otunjargych.tamtam.di.module

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.otunjargych.tamtam.di.repo.auth.AuthRepository
import org.otunjargych.tamtam.di.repo.auth.AuthRepositoryImpl
import org.otunjargych.tamtam.di.repo.location.LocationRepository
import org.otunjargych.tamtam.di.repo.location.LocationRepositoryImpl
import org.otunjargych.tamtam.di.repo.note.NotesRepository
import org.otunjargych.tamtam.di.repo.note.NotesRepositoryImpl
import org.otunjargych.tamtam.di.repo.story.StoryRepository
import org.otunjargych.tamtam.di.repo.story.StoryRepositoryImpl
import org.otunjargych.tamtam.di.repo.user.UserRepository
import org.otunjargych.tamtam.di.repo.user.UserRepositoryImpl

val repoModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single<NotesRepository> { NotesRepositoryImpl(get(), get()) }
    single<StoryRepository> { StoryRepositoryImpl(get()) }
}