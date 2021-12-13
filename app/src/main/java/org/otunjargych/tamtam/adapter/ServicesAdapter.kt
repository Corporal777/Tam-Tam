package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import kotlinx.android.synthetic.main.list_item.view.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.extensions.DiffUtilCallbackS
import org.otunjargych.tamtam.model.Services
import java.text.SimpleDateFormat
import java.util.*

class ServicesAdapter : RecyclerView.Adapter<ServicesAdapter.ViewHolder>() {

    private var listAds = mutableListOf<Services>()
    private lateinit var onClickListener: OnAdClickListener
    private lateinit var mDiffResult: DiffUtil.DiffResult

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    fun update(
        list: MutableList<Services>?,
        onClickListener: OnAdClickListener
    ) {
        this.listAds = list!!
        this.onClickListener = onClickListener
//        listAds.sortBy {
//            it.timeStamp.toString()
//        }
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackS(listAds, list))
        mDiffResult.dispatchUpdatesTo(this)

    }


    interface OnAdClickListener {
        fun onAdClick(services: Services, position: Int)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listAds[position])
    }

    override fun getItemCount(): Int {
        return listAds.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(services: Services) = with(itemView) {


            if (services.text.length > 56) {
                tv_text.text =
                    StringBuilder(
                        services.text.substring(0, 55).replace("\n", " ").toLowerCase()).append("...").toString()
            } else
                tv_text.text = services.text

            if (services.salary == "" || services.salary == null) {
                tv_salary.text = "Договорная"
                iv_currency.visibility = View.GONE
            } else
                tv_salary.text = services.salary

            tv_date.text = services.timeStamp.toString().asTime()

            if (services.station == "" || services.station == null) {
                tv_location.text = "Не указано"
            } else
                tv_location.text = "м. " + services.station

            tv_category.text = services.category
            if (services.imageURL != "") {
                iv_work_image.load(services.imageURL)
            } else iv_work_image.load(R.drawable.placeholder)

            cv_item.setOnClickListener {
                onClickListener.onAdClick(services, position)

            }
        }
    }


    private fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return timeFormat.format(time)
    }

}