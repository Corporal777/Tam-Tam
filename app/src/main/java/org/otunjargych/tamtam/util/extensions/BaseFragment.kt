package org.otunjargych.tamtam.util.extensions

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import org.otunjargych.tamtam.R

abstract class BaseFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.tam_tam_motion_duration_medium).toLong()
        }
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.tam_tam_motion_duration_medium).toLong()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnPreDraw { startPostponedEnterTransition() }
    }




}