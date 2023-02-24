package org.otunjargych.tamtam.ui.createNote.items

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.model.request.*
import org.otunjargych.tamtam.util.findItemBy
import org.otunjargych.tamtam.util.updateItem

class MainDataGroup(
    private val context: Context,
    private val noteDraft: NoteDraftModel?
) : NestedGroup() {

    private val mainDataItem = MainDataItem(
        noteDraft?.name,
        noteDraft?.description,
        noteDraft?.category,
        noteDraft?.contacts?.phone,
        noteDraft?.contacts?.whatsapp
    ).apply {
        onCategoryChangeCallback = {
            when (it) {
                "work" -> additionalDataSection.updateItem(WorkAdditionalDataItem())
                "house" -> additionalDataSection.updateItem(HouseAdditionalDataItem())
            }
        }

    }
    private val additionalDataSection = Section().apply {
        setHeader(TitleDataItem(context.getString(R.string.additional_information)))
        setHideWhenEmpty(true)
    }

    init {
        if (noteDraft != null) {
            when (noteDraft.category) {
                "work" -> additionalDataSection.updateItem(
                    WorkAdditionalDataItem(
                        noteDraft.additionalData,
                        noteDraft.salary
                    )
                )
                "house" -> additionalDataSection.updateItem(
                    HouseAdditionalDataItem(
                        noteDraft.additionalData,
                        noteDraft.salary
                    )
                )
            }
        }
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

    fun isMainDataValid() = mainDataItem.isMainDataValid()
    fun isFieldsEmpty() = mainDataItem.isFieldsIsEmpty()
    fun isAdditionalDataValid(): Boolean {
        val item = additionalDataSection.findItemBy<AdditionalDataItem<*>> { true }
        return item?.isAdditionalDataValid() ?: false
    }

    fun getMainData() = mainDataItem.getMainData()
    fun getContactsData() = mainDataItem.getContactsData()

    fun getAdditionalData(): MutableMap<String, String> {
        val item = additionalDataSection.findItemBy<AdditionalDataItem<*>> { true }
        return item?.getDataToSave() ?: mutableMapOf()
    }

    fun getDraftData(addressData: NoteAddressModel?): NoteDraftModel {
        val mainData = getMainData()
        val salary = getAdditionalData()["salary"]
        val address = addressData ?: NoteAddressModel("", "", null, null, emptyList(), null, null)
        val additionalData =
            additionalDataSection.findItemBy<AdditionalDataItem<*>> { true }?.getDraftToSave()

        return NoteDraftModel(
            mainData["name"] ?: "",
            mainData["description"] ?: "",
            salary ?: "",
            mainData["category"] ?: "",
            getContactsData(),
            address,
            additionalData ?: ""
        )
    }

    override fun getGroupCount() = 2
}