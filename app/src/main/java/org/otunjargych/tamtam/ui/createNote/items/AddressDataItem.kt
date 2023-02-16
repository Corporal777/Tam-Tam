package org.otunjargych.tamtam.ui.createNote.items

import android.app.Activity
import android.util.Log
import android.widget.TextView
import androidx.core.view.isVisible
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
    city: String?
) : BindableItem<ItemAddressDataBinding>() {

    private var selectedCity = city ?: ""
    private var selectedRegion = ""
    private var selectedStations = arrayListOf<String>()

    private lateinit var mBinding: ItemAddressDataBinding

    override fun bind(viewBinding: ItemAddressDataBinding, position: Int) {
        mBinding = viewBinding
        viewBinding.apply {
            etCity.apply {
                tvCityRequired.isVisible = selectedCity.isNullOrBlank()
                setTextWithoutSearch(selectedCity)
                onDataChangedListener = {
                    tvCityRequired.apply {
                        if (!it.isNullOrBlank()) changeRequiredTextColor(true)
                        isVisible = it.isNullOrBlank()
                    }
                    selectedCity = it
                }
                onDataSelectedListener = {
                    selectedCity = it
                }
            }
            etRegion.initInput(selectedRegion) {
                selectedRegion = it.toString()
            }
            etStation.initInputClick(selectedStations.getStationsFromList()) {
                showMetroStations(selectedCity, selectedStations)
            }
        }
    }


    fun isAddressDataValid(): Boolean {
        var valid = true
        if (selectedCity.isNullOrBlank()) {
            valid = false
            mBinding.tvCityRequired.changeRequiredTextColor(false)
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

    private fun TextView.changeRequiredTextColor(isCorrect: Boolean) {
        if (!isCorrect) setTextColor(context.getColor(R.color.error_text_color))
        else setTextColor(context.getColor(R.color.app_main_color))
    }

    override fun getLayout(): Int = R.layout.item_address_data
}