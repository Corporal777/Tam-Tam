package org.otunjargych.tamtam.ui.town

import android.app.Activity
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentTownBinding
import org.otunjargych.tamtam.ui.base.BaseFragment
import org.otunjargych.tamtam.ui.interfaces.ToolbarFragment
import org.otunjargych.tamtam.util.locationUtils.LocationUtils
import kotlin.reflect.KClass


class TownFragment : BaseFragment<TownViewModel, FragmentTownBinding>(),
    ToolbarFragment {

    private lateinit var locationUtils: LocationUtils


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationUtils = LocationUtils(this)
        viewModel.initLocation(locationUtils.getLocation())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnYes.setOnClickListener {
                viewModel.saveUserCity()
            }
            btnNo.setOnClickListener {
                lnCurrentCity.isVisible = false
                lnChangeCity.isVisible = true
            }
            btnAccept.setOnClickListener {
                if (etCity.text.toString().isNullOrEmpty())
                    tilCity.error = getString(R.string.empty_field)
                else viewModel.saveUserCity()
            }
            etCity.apply {
                onDataSelectedListener = { viewModel.userTown = it }
                onDataChangedListener = { viewModel.userTown = it }
            }
        }
        observeUserLocation()
        observeTownSaved()
    }


    private fun observeUserLocation() {
        viewModel.userLocation.observe(viewLifecycleOwner) { location ->
            mBinding.apply {
                if (location == null) {
                    showToast(getString(R.string.error_location_not_determinet))
                    lnChangeCity.isVisible = true
                } else {
                    lnCurrentCity.isVisible = true
                    tvCityTitle.text = getString(R.string.is_your_city_is, location.city)
                }
            }
        }
    }


    private fun observeTownSaved() {
        viewModel.townSavedSuccess.observe(viewLifecycleOwner) {
            if (it) showHome()
        }
    }

    private fun showHome() {
        findNavController().navigate(R.id.home_fragment, null,
            navOptions {
                popUpTo(R.id.nav_graph) { inclusive = true }
            })
    }


    override val layoutId: Int = R.layout.fragment_town
    override fun getViewModelClass(): KClass<TownViewModel> = TownViewModel::class
    override val title: CharSequence by lazy { getString(R.string.city) }
    override fun toolbarIconsContainer(viewGroup: ViewGroup) {}


    override fun onDestroy() {
        super.onDestroy()
        locationUtils.clearFields()
    }
}