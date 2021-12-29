package org.otunjargych.tamtam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.station_item.view.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.model.Station


class StationsAdapter : RecyclerView.Adapter<StationsAdapter.StationsHolder>() {

    private var stationsList = ArrayList<Station>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.station_item, parent, false)
        return StationsHolder(view)
    }

    fun addStations(list: List<Station>) {
        stationsList.clear()
        stationsList.addAll(list)
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
            tv_station.text = station.title
        }
    }
}


