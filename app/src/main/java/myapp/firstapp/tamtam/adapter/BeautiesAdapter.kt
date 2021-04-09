package myapp.firstapp.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import myapp.firstapp.tamtam.extensions.DiffUtilCallbackB
import myapp.firstapp.tamtam.model.Beauty
import kotlinx.android.synthetic.main.list_item.view.*
import myapp.firstapp.tamtam.R
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class BeautiesAdapter : RecyclerView.Adapter<BeautiesAdapter.BeautyViewHolder>() {
    private var listAds = mutableListOf<Beauty>()
    private lateinit var onClickListener: OnAdClickListener
    private lateinit var mDiffResult: DiffUtil.DiffResult

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeautyViewHolder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return BeautyViewHolder(view)
    }

    fun update(list: MutableList<Beauty>, onItemClick: OnAdClickListener) {
        this.listAds = list
        onClickListener = onItemClick
        mDiffResult = DiffUtil.calculateDiff(DiffUtilCallbackB(listAds, list))
        mDiffResult.dispatchUpdatesTo(this)
    }

    interface OnAdClickListener {
        fun onItemClick(beauty: Beauty, position: Int)
    }

    override fun onBindViewHolder(holder: BeautyViewHolder, position: Int) {
        holder.bind(listAds[position])
    }

    override fun getItemCount(): Int = listAds.size

    inner class BeautyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(beauty: Beauty) = with(itemView) {
            if (beauty.text.length > 52) {
                tv_text.text =
                    StringBuilder(beauty.text.substring(0, 50).replace("\n", " ").toLowerCase()).append("...")
                        .toString()
            } else
                tv_text.text = beauty.text.trim()

            if (beauty.salary == "" || beauty.salary == null) {
                tv_salary.text = "Договорная"
                iv_currency.visibility = View.GONE
            } else
                tv_salary.text = beauty.salary

            if (beauty.station == "" || beauty.station == null) {
                tv_location.text = "Не указано"
            } else
                tv_location.text = "м. " + beauty.station

            tv_category.text = beauty.category
            tv_date.text = beauty.timeStamp.toString().asTime()
            if (beauty.firstImageURL != "") {
                iv_work_image.load(beauty.firstImageURL)
            } else iv_work_image.load(R.drawable.placeholder)
            cv_item.setOnClickListener {
                onClickListener.onItemClick(beauty, position)

            }
        }
    }

    private fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return timeFormat.format(time)
    }
}