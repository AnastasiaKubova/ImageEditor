package com.example.imageeditor.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.imageeditor.R
import com.example.imageeditor.ui.croppanel.CropPanelListener
import com.example.imageeditor.utility.DrawManager
import com.example.imageeditor.utility.ImagePickerManager
import com.google.android.material.bottomsheet.BottomSheetDialog

open class BaseFragment: Fragment() {

    private lateinit var dialog: BottomSheetDialog
    private lateinit var dialogView: View
    private val WRITE_REQUEST_CODE = 1
    private val READ_REQUEST_CODE = 2

    companion object {
        var cropListener: CropPanelListener? = null
    }

    fun initBottomSheet(activity: Activity) {

        /* Init bottom sheet dialog. */
        dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        dialog = BottomSheetDialog(activity)
        dialog.setContentView(dialogView)
    }

    fun showBottomSheetDialog() {
        dialogView.findViewById<TextView>(R.id.open_camera_bottom_sheet).setOnClickListener {
            ImagePickerManager.loadImageFromCamera(this)
            dialog.cancel()
        }
        dialogView.findViewById<TextView>(R.id.select_image_bottom_sheet).setOnClickListener {
            ImagePickerManager.loadImagesFromGallery(this)
            dialog.cancel()
        }
        dialogView.findViewById<TextView>(R.id.custom_picker_bottom_sheet).setOnClickListener {
            val permissions = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissions, READ_REQUEST_CODE)
            dialog.cancel()
        }
        dialogView.findViewById<TextView>(R.id.save_image_bottom_sheet).setOnClickListener {
            val permissions = arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permissions, WRITE_REQUEST_CODE)
            dialog.cancel()
        }
        dialog.show()
    }

    fun openCropPanel() {
        cropListener?.onCropStart()
        findNavController().popBackStack()
        findNavController().navigate(R.id.cropPanelFragment)
    }

    fun openMenuPanel() {
        cropListener?.onCropStop()
        findNavController().popBackStack()
        findNavController().navigate(R.id.mainPanelFragment)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_REQUEST_CODE -> if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                if (DrawManager.currentBitmap != null) {
                    ImagePickerManager.saveImageToGallery(requireContext(), DrawManager.currentBitmap!!)
                    Toast.makeText(context, getString(R.string.image_was_saved), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, getString(R.string.save_error), Toast.LENGTH_SHORT).show()
                }
            }
            READ_REQUEST_CODE -> {
                findNavController().popBackStack()
                findNavController().navigate(R.id.imagePickerFragment)
            }
        }
    }
}