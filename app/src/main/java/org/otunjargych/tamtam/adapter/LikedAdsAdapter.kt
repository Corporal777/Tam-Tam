package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import kotlinx.android.synthetic.main.list_item.view.tv_category
import kotlinx.android.synthetic.main.list_item.view.tv_date
import kotlinx.android.synthetic.main.list_item.view.tv_location
import kotlinx.android.synthetic.main.list_item.view.tv_salary
import kotlinx.android.synthetic.main.list_item.view.tv_text
import kotlinx.android.synthetic.main.swipe_item.view.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.model.LikedAds
import java.text.SimpleDateFormat
import java.util.*

class LikedAdsAdapter : RecyclerView.Adapter<LikedAdsAdapter.AdOneViewHolder>() {

    private var listAds = mutableListOf<LikedAds>()
    private var viewBinderHelper: ViewBinderHelper = ViewBinderHelper()
    private lateinit var onClickListener: OnAdClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdOneViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.swipe_item, parent, false)
        return AdOneViewHolder(view)
    }

    interface OnAdClickListener {
        fun onAdClick(likedAds: LikedAds, position: Int)
        fun onAdDelete(likedAds: LikedAds, position: Int)
    }


    fun update(list: MutableList<LikedAds>, onAdClickListener: OnAdClickListener) {
        listAds = list
        onClickListener = onAdClickListener
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdOneViewHolder, position: Int) {
        viewBinderHelper.setOpenOnlyOne(true)
        viewBinderHelper.bind(holder.swipeRevealLayout, listAds[position].title)

        viewBinderHelper.closeLayout(listAds[position].title)
        holder.bind(listAds[position])
    }

    override fun getItemCount(): Int {
        return listAds.size
    }

    inner class AdOneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val swipeRevealLayout: SwipeRevealLayout = itemView.findViewById(R.id.swipe_layout)

        fun bind(likedAds: LikedAds) = with(itemView) {

            if (likedAds.text.length > 63) {
                tv_text.text =
                    StringBuilder(likedAds.text.substring(0, 60).replace("\n", " ")).append("...").toString()
            } else
                tv_text.text = likedAds.text
            if (likedAds.salary == "" || likedAds.salary == null) {
                tv_salary.text = "Договорная"
            } else
                tv_salary.text = likedAds.salary
            if (likedAds.title.length > 20) {
                tv_title.text =
                    StringBuilder(likedAds.title.substring(0, 19).replace("\n", " ").toLowerCase()).append("...").toString()
            } else
                tv_title.text = likedAds.title
            tv_date.text = likedAds.timeStamp.toString().asTime()
            tv_location.text = "м. " + likedAds.station
            tv_category.text = likedAds.category

            cv_swipe_item.setOnClickListener {
                onClickListener.onAdClick(likedAds, position)
            }
            card_delete.setOnClickListener {
                onClickListener.onAdDelete(likedAds, position)
            }
        }
    }

    private fun String.asTime(): String {
        val time = Date(this.toLong())
        val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return timeFormat.format(time)
    }


}