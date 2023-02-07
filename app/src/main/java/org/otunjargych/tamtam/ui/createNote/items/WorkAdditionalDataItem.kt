package org.otunjargych.tamtam.ui.createNote.items

import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemWorkAdditionalDataBinding

class WorkAdditionalDataItem : AdditionalDataItem<ItemWorkAdditionalDataBinding>() {


    override fun bind(viewBinding: ItemWorkAdditionalDataBinding, position: Int) {

    }



    override fun getDataToSave(): MutableMap<String, String> {
        return mutableMapOf()
    }

    override fun getLayout(): Int = R.layout.item_work_additional_data
}