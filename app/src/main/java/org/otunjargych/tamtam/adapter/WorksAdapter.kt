package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.extensions.DiffUtilCallbackW
import org.otunjargych.tamtam.model.Work
import java.text.SimpleDateFormat
import java.util.*

class WorksAdapter : RecyclerView.Adapter<WorksAdapter.ViewHolder>() {

    private var listAds = mutableListOf<Work>()
    private lateinit var onClickListener: OnItemAdClickListener
    private lateinit var mDiffResult: DiffUtil.DiffResult

    fun update(list: List<Work>?, onClickListener: OnItemAdClickListener) {
        this.listAds = (list as MutableList<Work>?)!!
        this.onClickListener = onClickListener
//        listAds.sortBy {
//            it.timeStamp.toString()
//        }
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackW(listAds, list!!))
        mDiffResult.dispatchUpdatesTo(this)

    }

    interface OnItemAdClickListener {
        fun onAdClick(work: Work, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listAds[position])
    }

    override fun getItemCount(): Int {
        return listAds.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(work: Work) = with(itemView) {


            if (work.text.length > 80) {
                tv_text.text =
                    StringBuilder(work.text.substring(0, 79).replace("\n", " ").toLowerCase()).append("...")
                        .toString()
            } else
                tv_text.text = work.text
            if (work.salary == "" || work.salary == null) {
                tv_salary.text = "Договорная"
                iv_currency.visibility = View.GONE
            } else
                tv_salary.text = work.salary

            tv_date.text = work.timeStamp.toString().asTime()
            if (work.station == null || work.station == "") {
                tv_location.text = "Не указано"
            } else
                tv_location.text = "м. " + work.station
            tv_category.text = work.category
//            if (work.imageURL != "") {
//                iv_work_image.load(work.imageURL)
//            } else iv_work_image.load(R.drawable.placeholder)

            cv_item.setOnClickListener {
                onClickListener.onAdClick(work, position)

            }
        }
    }
}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return timeFormat.format(time)
}
