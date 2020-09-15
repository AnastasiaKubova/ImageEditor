package com.example.imageeditor.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.imageeditor.R
import com.example.imageeditor.utility.ImageManager
import com.google.android.material.bottomsheet.BottomSheetDialog

open class BaseFragment: Fragment() {

    private lateinit var dialog: BottomSheetDialog
    private lateinit var dialogView: View

    var currentImagePath: String? = null

    fun initBottomSheet(activity: Activity) {

        /* Init bottom sheet dialog. */
        dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        dialog = BottomSheetDialog(activity)
        dialog.setContentView(dialogView)
    }

    fun showBottomSheetDialog() {
        dialogView.findViewById<TextView>(R.id.open_camera_bottom_sheet).setOnClickListener {
            ImageManager.loadImageFromCamera(this)
            dialog.cancel()
        }
        dialogView.findViewById<TextView>(R.id.select_image_bottom_sheet).setOnClickListener {
            ImageManager.loadFromGallery(this)
            dialog.cancel()
        }
        dialogView.findViewById<TextView>(R.id.save_image_bottom_sheet).setOnClickListener {
            if (currentImagePath == null) {
                return@setOnClickListener
            }
            ImageManager.saveFileToGallery(requireContext(), currentImagePath!!)
            dialog.cancel()
        }
        dialog.show()
    }
}