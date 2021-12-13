package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.list_item.view.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.extensions.DiffUtilCallbackBS
import org.otunjargych.tamtam.model.BuySell
import java.text.SimpleDateFormat
import java.util.*

class BuySellAdapter : RecyclerView.Adapter<BuySellAdapter.BuySellViewHolder>() {

    private var listAds = mutableListOf<BuySell>()
    private lateinit var onClickListener: OnAdClickListener
    private lateinit var mDiffResult: DiffUtil.DiffResult


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuySellViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return BuySellViewHolder(view)
    }

    interface OnAdClickListener {
        fun onAdClick(buySell: BuySell, position: Int)
    }

    fun update(list: MutableList<BuySell>, onClickListener: OnAdClickListener) {
        listAds = list
        this.onClickListener = onClickListener
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackBS(listAds, list))
        mDiffResult.dispatchUpdatesTo(this)
    }


    override fun onBindViewHolder(holder: BuySellViewHolder, position: Int) {
        holder.bind(listAds[position])
    }

    override fun getItemCount(): Int {
        return listAds.size
    }

    inner class BuySellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(buySell: BuySell) = with(itemView) {

            if (buySell.text.length > 52) {
                tv_text.text =
                    StringBuilder(buySell.text.substring(0, 50).replace("\n", " ").toLowerCase()).append("...").toString()
            } else
                tv_text.text = buySell.text.trim()
            if (buySell.salary == "" || buySell.salary == null) {
                tv_salary.text = "Договорная"
                iv_currency.visibility = View.GONE
            } else
                tv_salary.text = buySell.salary

            tv_date.text = buySell.timeStamp.toString().asTime()
            if (buySell.station == "" || buySell.station == null) {
                tv_location.text = "Не указано"
            } else
                tv_location.text = "м. " + buySell.station

            tv_category.text = buySell.category
            if (buySell.firstImageURL != "") {
                iv_work_image.load(buySell.firstImageURL)
            } else iv_work_image.load(R.drawable.placeholder)

            cv_item.setOnClickListener {
                onClickListener.onAdClick(buySell, position)

            }
        }
    }

    private fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return timeFormat.format(time)
    }
}