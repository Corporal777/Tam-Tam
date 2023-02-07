package org.otunjargych.tamtam.ui.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentMetroStationsBinding
import org.otunjargych.tamtam.ui.base.BaseBottomSheetFragment
import org.otunjargych.tamtam.ui.location.items.MetroStationItem
import org.otunjargych.tamtam.ui.town.CitiesViewModel
import org.otunjargych.tamtam.ui.views.SimpleTextWatcher
import org.otunjargych.tamtam.util.initInput
import org.otunjargych.tamtam.util.onTextChanged
import org.otunjargych.tamtam.viewmodel.LocationViewModel
import kotlin.reflect.KClass

class MetroStationsFragment(
    private val city: String?,
    private val selectedStations: List<String>
) : BaseBottomSheetFragment<MetroStationsViewModel, FragmentMetroStationsBinding>(true) {

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()


    private val simpleTextWatcher = SimpleTextWatcher().setAfterTextChangeRunnable {
        viewModel.searchStation(it.toString())
    }

    private var onSelectedStations: (list: List<String>) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadMetroStations(city, selectedStations)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            metroStationsList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
            etStation.apply {
                addTextChangedListener(simpleTextWatcher)
            }
            btnAccept.setOnClickListener {
                onSelectedStations.invoke(viewModel.getSelectedStations())
            }
        }
        observeStations()
        observeSelectedStations()
    }

    private fun observeStations() {
        viewModel.metroStations.observe(viewLifecycleOwner) { list ->
            if (!list.isNullOrEmpty()) {
                groupAdapter.update(list.map {
                    MetroStationItem(
                        it.name,
                        it.color,
                        viewModel.getSelectedStations()
                    ) { station ->
                        viewModel.selectStation(station)
                    }
                })
            }
        }
    }

    private fun observeSelectedStations() {
        viewModel.selectedStations.observe(viewLifecycleOwner) { list ->
            mBinding.apply {
                lnSelectedStations.isVisible = !list.isNullOrEmpty()
                //btnAccept.isEnabled = !list.isNullOrEmpty()
                tvSelectedStations.apply {
                    text = list.joinToString("\n") { it }
                }
            }
        }
    }

    fun setSelectedStationsCallback(block: (list: List<String>) -> Unit): MetroStationsFragment {
        onSelectedStations = block
        return this
    }

    override val layoutId: Int = R.layout.fragment_metro_stations
    override fun getViewModelClass(): KClass<MetroStationsViewModel> = MetroStationsViewModel::class
}