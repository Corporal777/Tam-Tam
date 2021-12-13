package org.otunjargych.tamtam.fragments.dialog_fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.adapter.CategoryAdapter

class CategoryDialogFragment : DialogFragment() {

    private lateinit var mRecyclerViewCategory: RecyclerView
    private lateinit var mAdapter: CategoryAdapter
    private var fragmentSendCategoryListener: OnFragmentSendCategoryListener? = null


    interface OnFragmentSendCategoryListener {
        fun onSendCategory(category: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentSendCategoryListener = try {
            context as OnFragmentSendCategoryListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Error")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_category, null)
        mRecyclerViewCategory = view.findViewById(R.id.category_list)

        initRV()
        return AlertDialog.Builder(activity)
            .setView(view)
            .create()
    }

    private fun initRV() {
        mRecyclerViewCategory.layoutManager = LinearLayoutManager(activity)
        mAdapter = CategoryAdapter()
        mRecyclerViewCategory.adapter = mAdapter

        val categories = ArrayList<String>()
        categories.add("Работа, Подработки")
        categories.add("Транспорт, Перевозки")
        categories.add("Медицина, Красота")
        categories.add("Продажа, Покупка")
        categories.add("Квартиры, Гостиницы")
        categories.add("Обучение, Услуги")


        val categoryClickListener: CategoryAdapter.OnCategoryClickListener =
            object : CategoryAdapter.OnCategoryClickListener {
                override fun onCategoryClick(category: String, position: Int) {
                    fragmentSendCategoryListener?.onSendCategory(category)
                }
            }
        mAdapter.update(categories, categoryClickListener)

    }
}