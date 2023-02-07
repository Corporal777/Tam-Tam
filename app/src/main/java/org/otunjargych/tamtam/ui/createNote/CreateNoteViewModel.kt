package org.otunjargych.tamtam.ui.createNote

import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.ui.base.BaseViewModel

class CreateNoteViewModel(private val appData: AppData) : BaseViewModel() {


    fun getCurrentTown() = appData.getUserTown()
}