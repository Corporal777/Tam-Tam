package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.list_item.view.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.extensions.DiffUtilCallbackT
import org.otunjargych.tamtam.model.Transportation
import java.text.SimpleDateFormat
import java.util.*

class TransportAdapter : RecyclerView.Adapter<TransportAdapter.ViewHolder>() {

    private var listAds = mutableListOf<Transportation>()
    private lateinit var onClickListener: OnAdClickListener
    private lateinit var mDiffResult: DiffUtil.DiffResult

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    fun update(
        list: MutableList<Transportation>?,
        onClickListener: OnAdClickListener
    ) {
        this.listAds = list!!
        this.onClickListener = onClickListener
//        listAds.sortBy {
//            it.timeStamp.toString()
//        }
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackT(listAds, list))
        mDiffResult.dispatchUpdatesTo(this)

    }


    interface OnAdClickListener {
        fun onAdClick(transport: Transportation, position: Int)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listAds[position])
    }

    override fun getItemCount(): Int {
        return listAds.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(transport: Transportation) = with(itemView) {


            if (transport.text.length > 49) {
                tv_text.text =
                    StringBuilder(
                        transport.text.substring(0, 48).replace("\n", " ").toLowerCase()
                    ).append("...").toString()
            } else
                tv_text.text = transport.text
            if (transport.salary == "" || transport.salary == null) {
                tv_salary.text = "Договорная"
                iv_currency.visibility = View.GONE
            } else
                tv_salary.text = transport.salary

            tv_date.text = transport.timeStamp.toString().asTime()
            if (transport.station == "" || transport.station == null) {
                tv_location.text = "Не указано"
            } else
                tv_location.text = "м. " + transport.station
            tv_category.text = transport.category
            if (transport.imageURL != "") {
                iv_work_image.load(transport.imageURL)
            } else iv_work_image.load(R.drawable.placeholder)

            cv_item.setOnClickListener {
                onClickListener.onAdClick(transport, position)

            }
        }
    }


    private fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return timeFormat.format(time)
    }

}