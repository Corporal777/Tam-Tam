package org.otunjargych.tamtam.di.module

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.otunjargych.tamtam.ui.auth.login.LoginViewModel
import org.otunjargych.tamtam.ui.auth.register.RegisterViewModel
import org.otunjargych.tamtam.ui.confirmCode.ConfirmCodeViewModel
import org.otunjargych.tamtam.ui.profileSettings.changePassword.ChangePasswordViewModel
import org.otunjargych.tamtam.ui.createNote.CreateNoteViewModel
import org.otunjargych.tamtam.ui.detailNote.NoteDetailViewModel
import org.otunjargych.tamtam.ui.favoriteNotes.FavoriteNotesViewModel
import org.otunjargych.tamtam.ui.home.HomeViewModel
import org.otunjargych.tamtam.ui.location.MetroStationsViewModel
import org.otunjargych.tamtam.ui.main.MainViewModel
import org.otunjargych.tamtam.ui.my.MyNotesViewModel
import org.otunjargych.tamtam.ui.profile.ProfileViewModel
import org.otunjargych.tamtam.ui.profileSettings.ProfileSettingsViewModel
import org.otunjargych.tamtam.ui.profileSettings.changeLogin.ChangeLoginViewModel
import org.otunjargych.tamtam.ui.splash.SplashViewModel
import org.otunjargych.tamtam.ui.stories.StoriesViewModel
import org.otunjargych.tamtam.ui.town.CitiesViewModel
import org.otunjargych.tamtam.ui.town.TownViewModel

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { RegisterViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { ProfileSettingsViewModel(get(), get(), get()) }
    viewModel { TownViewModel(get(), get(), get()) }
    viewModel { CitiesViewModel(get()) }
    viewModel { CreateNoteViewModel(get(), get(), get()) }
    viewModel { MetroStationsViewModel(get(), get()) }
    viewModel { StoriesViewModel() }
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { ChangeLoginViewModel(get(), get()) }
    viewModel { ConfirmCodeViewModel(get()) }
    viewModel { SplashViewModel() }
    viewModel { MyNotesViewModel(get(), get()) }
    viewModel { FavoriteNotesViewModel(get(), get()) }
    viewModel { NoteDetailViewModel(get(), get()) }
}