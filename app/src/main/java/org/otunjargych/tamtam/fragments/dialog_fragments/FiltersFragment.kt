package org.otunjargych.tamtam.fragments.dialog_fragments

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ListPopupWindow
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.FragmentFiltersBinding

import org.otunjargych.tamtam.util.extensions.LocalSourceMetroStations
import org.otunjargych.tamtam.util.extensions.boom.Boom


class FiltersFragment(val onData: (String) -> Unit) : BottomSheetDialogFragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!
    private lateinit var mListPopupWindow: ListPopupWindow
    private var mSelectedStation: String = ""
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet: BottomSheetDialog =
            super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        val view: View = View.inflate(requireContext(), R.layout.fragment_filters, null)
        bottomSheet.setContentView(view)
        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        (bottomSheetBehavior as BottomSheetBehavior<*>).peekHeight =
            BottomSheetBehavior.PEEK_HEIGHT_AUTO;
        bottomSheet.setOnShowListener {
            setupFullHeight(bottomSheet)
        }
        return bottomSheet
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.customTitle.text = "Фильтры"
        binding.toolbar.setNavigationOnClickListener {
            dismiss()
        }

        val args = arguments
        val category = args?.getString("category")
        binding.tvCategory.text = category
        val bundle: Bundle? = this.arguments
        val str: String? = bundle?.getSerializable("category") as String?

        binding.tvCategory.text = str
        binding.tvMetro.setOnClickListener {
            showMetroStationsList()
        }
        Boom(binding.btnApply)
        binding.btnApply.setOnClickListener {
            onData(mSelectedStation)
            dismiss()
        }
    }

    private fun showMetroStationsList() {
        val list = LocalSourceMetroStations().getMetroStationsList()
        mListPopupWindow = ListPopupWindow(requireContext())
        mListPopupWindow.apply {
            anchorView = binding.tvMetro
            width = 640
            height = 800
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.station_item, R.id.tv_station, list
                )
            )
            isModal = true
            setOnItemClickListener { _, _, position, _ ->
                binding.tvMetro.text = list[position]
                mSelectedStation = list[position]
                dismiss()
            }
        }
        mListPopupWindow.show()
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet: FrameLayout =
            dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        showFullScreenBottomSheet(bottomSheet)
        bottomSheet.setBackgroundResource(android.R.color.transparent)
    }

    private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = Resources.getSystem().displayMetrics.heightPixels
        bottomSheet.layoutParams = layoutParams
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}