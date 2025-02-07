package com.amaze.filemanager.ui.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.amaze.filemanager.R
import com.amaze.filemanager.ui.activities.superclasses.ThemedActivity
import com.amaze.filemanager.ui.theme.AppTheme
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseBottomSheetFragment : BottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState) as BottomSheetDialog
    }

    /**
     * Initializes bottom sheet ui resources based on current theme
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun initDialogResources(rootView: View) {
        when ((requireActivity() as ThemedActivity).getAppTheme()) {
            AppTheme.DARK -> {
                rootView.setBackgroundDrawable(
                    context?.resources?.getDrawable(
                        R.drawable.shape_dialog_bottomsheet_dark,
                    ),
                )
            }

            AppTheme.BLACK -> {
                rootView.setBackgroundDrawable(
                    context?.resources?.getDrawable(
                        R.drawable.shape_dialog_bottomsheet_black,
                    ),
                )
            }

            AppTheme.LIGHT -> {
                rootView
                    .setBackgroundDrawable(
                        context?.resources?.getDrawable(
                            R.drawable.shape_dialog_bottomsheet_white,
                        ),
                    )
            }
        }
    }
}
