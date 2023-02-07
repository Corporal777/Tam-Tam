package org.otunjargych.tamtam.ui.views.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.BottomSheetChangePhotoBinding
import java.util.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.collections.HashSet

class ChangePhotoBottomSheetDialog() : BottomSheetDialogFragment() {

    private var _binding: BottomSheetChangePhotoBinding? = null
    private val mBinding get() = _binding!!

    private var onActionGalleryClick: () -> Unit = {}
    private var onActionCameraClick: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetChangePhotoBinding.inflate(inflater, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnOpenGallery.setOnClickListener {
                onActionGalleryClick.invoke()
                dismiss()
            }
            btnOpenCamera.setOnClickListener {
                onActionCameraClick.invoke()
                dismiss()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }
        }

    }

    fun setActionGalleryCallback(block : () -> Unit): ChangePhotoBottomSheetDialog {
        onActionGalleryClick = block
        return this
    }

    fun setActionCameraCallback(block : () -> Unit) : ChangePhotoBottomSheetDialog {
        onActionCameraClick = block
        return this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}