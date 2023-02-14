package org.otunjargych.tamtam.ui.views.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.otunjargych.tamtam.databinding.BottomSheetNoteCreatedBinding

class NoteCreatedBottomSheetDialog(val context: Context) {

    private var onActionClick: () -> Unit = {}

    private val mBinding = BottomSheetNoteCreatedBinding.inflate(LayoutInflater.from(context))
    private var mDialog = BottomSheetDialog(context)

    init {
        mDialog.setContentView(mBinding.root)
        mDialog.setCancelable(false)

        mBinding.btnGoHome.setOnClickListener {
            onActionClick.invoke()
            mDialog.dismiss()
        }

        mDialog.show()
    }

    fun setActionCallback(block: () -> Unit): NoteCreatedBottomSheetDialog {
        onActionClick = block
        return this
    }


}