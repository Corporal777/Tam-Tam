package org.otunjargych.tamtam.ui.createNote.items

import android.content.Context
import android.util.Log
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.model.request.NoteContactsModel
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.util.findItemBy

class MainDataGroup(private val context: Context) : NestedGroup() {

    private val mainDataItem = MainDataItem().apply {
        onCategoryChangeCallback = {
            when (it) {
                "work" -> additionalDataSection.update(listOf(WorkAdditionalDataItem()))
                "house" -> {}
            }
        }
    }
    private val additionalDataSection = Section().apply {
        setHeader(TitleDataItem(context.getString(R.string.additional_information)))
        setHideWhenEmpty(true)
    }

    init {
        mainDataItem.registerGroupDataObserver(this)
        additionalDataSection.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> mainDataItem
            1 -> additionalDataSection
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            mainDataItem -> 0
            additionalDataSection -> 1
            else -> -1
        }
    }

    fun isMainDataValid(): Boolean {
        return mainDataItem.isMainDataValid()
    }

    fun getMainData(): MutableMap<String, String> {
        return mainDataItem.getMainData()
    }

    fun getContactsData(): NoteContactsModel {
        return mainDataItem.getContactsData()
    }

    fun isAdditionalDataValid(): Boolean {
        val item = additionalDataSection.findItemBy<AdditionalDataItem<*>> { true }
        return item?.isAdditionalDataValid() ?: false
    }

    fun getAdditionalData(): MutableMap<String, String> {
        val item = additionalDataSection.findItemBy<AdditionalDataItem<*>> { true }
        if (item != null) return item.getDataToSave()
        else return mutableMapOf()
    }


    override fun getGroupCount() = 2
}