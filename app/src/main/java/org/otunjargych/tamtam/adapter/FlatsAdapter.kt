package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.model.Flats
import java.text.SimpleDateFormat
import java.util.*

class FlatsAdapter : RecyclerView.Adapter<FlatsAdapter.FlatsViewHolder>() {

    private var flatsList = mutableListOf<Flats>()
    private lateinit var onClickListener: OnAdClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlatsViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return FlatsViewHolder(view)
    }

    fun update(list: MutableList<Flats>, onClickListener: OnAdClickListener) {
        flatsList = list
        this.onClickListener = onClickListener

    }

    interface OnAdClickListener {
        fun onAdClick(flats: Flats, position: Int)
    }

    override fun onBindViewHolder(holder: FlatsViewHolder, position: Int) {
        holder.bind(flatsList[position])
    }

    override fun getItemCount(): Int {
        return flatsList.size
    }

    inner class FlatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(flats: Flats) = with(itemView) {


            if (flats.text.length > 49) {
                tv_text.text =
                    StringBuilder(flats.text.substring(0, 48).replace("\n", " ").toLowerCase()).append("...").toString()
            } else
                tv_text.text = flats.text
            if (flats.salary == "" || flats.salary == null) {
                tv_salary.text = "Договорная"
                iv_currency.visibility = View.GONE
            } else
                tv_salary.text = flats.salary

            tv_date.text = flats.timeStamp.toString().asTime()

            if (flats.station == null || flats.station == "") {
                tv_location.text = "Не указано"
            } else
                tv_location.text = "м. " + flats.station
            tv_category.text = flats.category
//            if (flats.imageURL != "") {
//                iv_work_image.load(flats.imageURL)
//            } else iv_work_image.load(R.drawable.placeholder)

            cv_item.setOnClickListener {
                onClickListener.onAdClick(flats, position)

            }
        }
    }

    private fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return timeFormat.format(time)
    }

}