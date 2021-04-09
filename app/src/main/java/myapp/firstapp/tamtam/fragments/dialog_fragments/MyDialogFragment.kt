package myapp.firstapp.tamtam.fragments.dialog_fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import myapp.firstapp.tamtam.model.Station
import myapp.firstapp.tamtam.viewmodel.LocationViewModel
import myapp.firstapp.tamtam.R
import myapp.firstapp.tamtam.adapter.StationsAdapter


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
        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        viewModel.metroStationsList.observe(activity!!, Observer {

            val stateClickListener: StationsAdapter.OnStationClickListener =
                object : StationsAdapter.OnStationClickListener {
                    override fun onStationClick(station: Station, position: Int) {
                        if (station != null) {
                            fragmentSendDataListener?.onSendData(station.title)
                        }
                    }
                }
            if (it != null) {
                adapter.addStations(it.stations, stateClickListener)
            } else Toast.makeText(activity, "No internet", Toast.LENGTH_LONG).show()
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getContent()
    }
}