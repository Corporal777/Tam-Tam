package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.station_item.view.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.model.Station


class StationsAdapter : RecyclerView.Adapter<StationsAdapter.StationsHolder>() {

    private var stationsList: List<Station> = ArrayList()
    private lateinit var onClickListener: OnStationClickListener


    interface OnStationClickListener {
        fun onStationClick(station: Station, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.station_item, parent, false)
        return StationsHolder(view)
    }

    fun addStations(list: List<Station>, onClickListener: OnStationClickListener) {
        stationsList = list
        this.onClickListener = onClickListener
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: StationsHolder, position: Int) {
        holder.bind(stationsList[position])
    }

    override fun getItemCount(): Int {
        return stationsList.size
    }

    inner class StationsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(station: Station) = with(itemView) {
            tv_name_stations.text = station.title

            chosen_station.setOnClickListener {
                iv_chosen_icon.visibility = View.VISIBLE
                onClickListener.onStationClick(station, position)
            }
        }
    }
}


