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
import org.otunjargych.tamtam.adapter.StationsAdapter
import org.otunjargych.tamtam.model.Station
import org.otunjargych.tamtam.viewmodel.LocationViewModel


class MyDialogFragment : DialogFragment() {

    private lateinit var adapter: StationsAdapter
    private var viewModel = LocationViewModel()
    private lateinit var recyclerView: RecyclerView

    private var fragmentSendDataListener: OnFragmentSendDataListener? = null
    private var countries = ArrayList<Station>()


    interface OnFragmentSendDataListener {
        fun onSendData(data: String?)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentSendDataListener = try {
            context as OnFragmentSendDataListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context должен реализовывать интерфейс OnFragmentInteractionListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_stations, null)
        recyclerView = view.findViewById(R.id.stations_list)
        initRV()
        initViewModel()

        return AlertDialog.Builder(activity)
            .setView(view)
            .create()
    }

    private fun initRV() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = StationsAdapter()
        recyclerView.adapter = adapter
    }

    private fun initViewModel() {
    }

    override fun onResume() {
        super.onResume()
        viewModel.getContent()
    }
}