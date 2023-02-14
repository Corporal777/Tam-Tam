package org.otunjargych.tamtam.ui.createNote.items

import android.app.Activity
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemAddressDataBinding
import org.otunjargych.tamtam.model.request.NoteAddressModel
import org.otunjargych.tamtam.ui.location.MetroStationsFragment
import org.otunjargych.tamtam.util.initInput
import org.otunjargych.tamtam.util.initInputClick

class AddressDataItem(
    private val context: FragmentActivity,
    private val city: String?
) : BindableItem<ItemAddressDataBinding>() {

    private var selectedCity = city ?: ""
    private var selectedRegion = ""
    private var selectedStations = arrayListOf<String>()

    private lateinit var mBinding: ItemAddressDataBinding

    override fun bind(viewBinding: ItemAddressDataBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            etCity.apply {
                setTextWithoutSearch(city)
                onDataSelectedListener = {
                    tilCity.error = null
                    selectedCity = it
                }
            }
            etRegion.initInput(selectedRegion) {
                selectedRegion = it.toString()
            }
            etStation.initInputClick(selectedStations.getStationsFromList()) {
                showMetroStations(selectedCity, selectedStations)
            }
            isAddressDataValid()
        }
    }


    fun isAddressDataValid(): Boolean {
        var valid = true
        if (selectedCity.isNullOrBlank()) {
            valid = false
            mBinding.tilCity.error = mBinding.root.context.getString(R.string.require_enter)
        }
        return valid
    }

    fun getAddressData(): NoteAddressModel {
        return NoteAddressModel(
            selectedRegion,
            selectedCity,
            null,
            null,
            selectedStations,
            null,
            null
        )
    }

    private fun showMetroStations(city: String?, list: List<String>) {
        val stationsFragment = MetroStationsFragment(city, list)
        stationsFragment.show(context.supportFragmentManager, "metro_stations")
        stationsFragment.setSelectedStationsCallback {
            selectedStations.clear()
            selectedStations.addAll(it)
            stationsFragment.dismiss()
            mBinding.etStation.setText(selectedStations.getStationsFromList())
        }
    }

    private fun List<String>.getStationsFromList(): String? {
        return if (selectedStations.isNullOrEmpty()) null
        else selectedStations.joinToString(", ") { s -> s }
    }

    override fun getLayout(): Int = R.layout.item_address_data
}