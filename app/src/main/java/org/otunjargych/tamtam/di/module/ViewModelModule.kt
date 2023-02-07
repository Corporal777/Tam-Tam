package org.otunjargych.tamtam.di.module

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.otunjargych.tamtam.ui.auth.login.LoginViewModel
import org.otunjargych.tamtam.ui.auth.register.RegisterViewModel
import org.otunjargych.tamtam.ui.createNote.CreateNoteViewModel
import org.otunjargych.tamtam.ui.home.HomeViewModel
import org.otunjargych.tamtam.ui.location.MetroStationsViewModel
import org.otunjargych.tamtam.ui.main.MainViewModel
import org.otunjargych.tamtam.ui.profile.ProfileViewModel
import org.otunjargych.tamtam.ui.profileSettings.ProfileSettingsViewModel
import org.otunjargych.tamtam.ui.town.CitiesViewModel
import org.otunjargych.tamtam.ui.town.TownViewModel

val viewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { RegisterViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { ProfileSettingsViewModel(get(), get(), get()) }
    viewModel { TownViewModel(get(), get(), get()) }
    viewModel { CitiesViewModel(get()) }
    viewModel { CreateNoteViewModel(get()) }
    viewModel { MetroStationsViewModel(get(), get()) }
}