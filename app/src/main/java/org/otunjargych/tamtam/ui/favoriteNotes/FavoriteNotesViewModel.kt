package org.otunjargych.tamtam.ui.favoriteNotes

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.plusAssign
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.di.repo.note.NotesRepository
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.ui.base.BaseViewModel
import org.otunjargych.tamtam.util.extensions.*
import org.otunjargych.tamtam.util.toSingleEvent
import java.util.*
import kotlin.collections.ArrayList

class FavoriteNotesViewModel(
    private val appData: AppData,
    private val notesRepository: NotesRepository
) : BaseViewModel() {

    private val selectedFilters = arrayListOf<String>()
    private var filterLoading = false

    private val _notesList = MutableLiveData<List<NoteModel?>>()
    val notesList = _notesList.toSingleEvent()

    private val _placeholder = MutableLiveData<String>()
    val placeholder = _placeholder.toSingleEvent()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        if (!filterLoading) _notesList.postValue(List(6) { null })
        compositeDisposable += notesRepository.getNotesListByParams(buildFilters())
            .performOnBackgroundOutOnMain()
            .let {
                if (filterLoading) {
                    filterLoading = false
                    it.withLoadingDialog(progressData)
                } else it
            }
            .subscribeSimple(
                onError = {
                    _placeholder.postValue("В избранном пусто")
                    it.printStackTrace()
                },
                onSuccess = {
                    if (it.isEmpty()) _placeholder.postValue("В избранном пусто")
                    else if (it.isEmpty() && !selectedFilters.isNullOrEmpty())
                        _placeholder.postValue("Нет результатов")
                    else _notesList.postValue(it)
                })

    }

    fun onFilterChanged(filter: FavoriteFilter) {
        if (filter.isChecked) selectedFilters.add(filter.name)
        else {
            if (selectedFilters.contains(filter.name)) selectedFilters.remove(filter.name)
        }
        filterLoading = true
        loadNotes()
    }


    private fun buildFilters(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            put(FILTER_FAVORITE, appData.getUserId())
            if (!selectedFilters.isNullOrEmpty())
                put(FILTER_SUBCATEGORY, selectedFilters.joinToString(",") { it })
        }
    }

    fun getFavoriteFilters(): List<FavoriteFilter> {
        return listOf<FavoriteFilter>(
            FavoriteFilter(1, "Вакансия", false),
            FavoriteFilter(2, "Подработка", false),
            FavoriteFilter(3, "Квартира", false),
            FavoriteFilter(4, "Комната", false),
            FavoriteFilter(5, "Авто", false),
            FavoriteFilter(6, "Услуги", false),
        )
    }
}

class FavoriteFilter(
    val id: Int,
    val name: String,
    var isChecked: Boolean
)