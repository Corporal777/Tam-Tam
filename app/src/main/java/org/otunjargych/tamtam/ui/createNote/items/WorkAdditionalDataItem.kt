package org.otunjargych.tamtam.ui.createNote.items

import android.widget.TextView
import androidx.core.view.isVisible
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemWorkAdditionalDataBinding
import org.otunjargych.tamtam.ui.views.adapters.NoFilterArrayAdapter
import org.otunjargych.tamtam.util.extensions.getWorkSchedulesList
import org.otunjargych.tamtam.util.extensions.getWorkSubCategoriesList
import org.otunjargych.tamtam.util.initInput

class WorkAdditionalDataItem : AdditionalDataItem<ItemWorkAdditionalDataBinding>() {

    private var subCategory = ""
    private var workSchedule = ""
    private var workExperience = ""
    private var workSalary = ""
    private var workComment = ""

    private lateinit var mBinding : ItemWorkAdditionalDataBinding
    override fun bind(viewBinding: ItemWorkAdditionalDataBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            etSubCategory.apply {
                keyListener = null
                setAdapter(
                    NoFilterArrayAdapter(
                        context,
                        R.layout.layout_town,
                        android.R.id.text1,
                        getWorkSubCategoriesList()
                    )

                )
                initInput(subCategory) {
                    tvNoteSubCategoryRequired.apply {
                        if (!it.toString().isNullOrBlank()) changeRequiredTextColor(true)
                        isVisible = it.toString().isNullOrBlank()
                    }
                    subCategory = it.toString()
                }
            }
            etSchedule.apply {
                keyListener = null
                setAdapter(
                    NoFilterArrayAdapter(
                        context,
                        R.layout.layout_town,
                        android.R.id.text1,
                        getWorkSchedulesList()
                    )

                )
                initInput(workSchedule) {
                    workSchedule = it.toString()
                }
            }

            etExperience.initInput(workExperience) {
                workExperience = it.toString()
            }

            etSalary.initInput(workSalary) {
                workSalary = it.toString()
            }
            etComment.initInput(workComment) {
                workComment = it.toString()
            }
        }
    }


    override fun isAdditionalDataValid(): Boolean {
        var valid = true
        if (subCategory.isNullOrBlank()){
            valid = false
            mBinding.tvNoteSubCategoryRequired.changeRequiredTextColor(false)
        }
        return valid
    }

    override fun getDataToSave(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            put("subCategory", subCategory)
            put("workSchedule", workSchedule)
            put("workExperience", workExperience)
            put("workComment", workComment)
            put("salary", workSalary)
        }
    }

    private fun TextView.changeRequiredTextColor(isCorrect: Boolean) {
        if (!isCorrect) setTextColor(context.getColor(R.color.error_text_color))
        else setTextColor(context.getColor(R.color.app_main_color))
    }


    override fun getLayout(): Int = R.layout.item_work_additional_data
}