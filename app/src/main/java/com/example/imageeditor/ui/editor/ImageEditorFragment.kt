package com.example.imageeditor.ui.editor

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.view.MotionEvent.ACTION_MOVE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.imageeditor.R
import com.example.imageeditor.ui.BaseFragment
import com.example.imageeditor.ui.croppanel.CropPanelFragment
import com.example.imageeditor.ui.custom.CustomImageView
import com.example.imageeditor.ui.imagepicker.ImagePickerFragment
import com.example.imageeditor.ui.mainpanel.MainPanelFragment
import com.example.imageeditor.utility.ImagePickerManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.image_editot_fragment.*


class ImageEditorFragment: BaseFragment(), CropPanelFragment.CropPanelListener,
    ImagePickerFragment.ImagePickerListener, MainPanelFragment.MenuPanelListener {

    companion object {
        val instance = ImageEditorFragment()
    }

    private lateinit var dialog: BottomSheetDialog
    private lateinit var dialogView: View
    private val WRITE_REQUEST_CODE = 1
    private val READ_REQUEST_CODE = 2
    private val viewModel by viewModels<ImageEditorViewModel>()

   val args: ImageEditorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.image_editot_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Init menu. */
        setHasOptionsMenu(true)
        changeFragmentListener?.onChangeFragment(false)

        /* Init bottom sheet. */
        initBottomSheet(requireActivity())

        /* Init observ.*/
        initObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val b = args.selectedBitmap
        if (b != null) {
            image_preview_editor.setImageBitmap(b)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu -> {
                showBottomSheetDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) {
            return
        }
        var bitmap: Bitmap? = null

        /* From camera. */
        if (requestCode == ImagePickerManager.REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data.extras == null) {
                return
            }
            bitmap = data.extras!!.get("data") as Bitmap
        }

        /* From default picker. */
        else if (requestCode == ImagePickerManager.PICK_IMAGE) {
            if (data.data == null) {
                return
            }
            val imageStream = requireActivity().contentResolver.openInputStream(data.data!!)
            if (imageStream != null) {
                bitmap = ImagePickerManager.createBitmap(imageStream)
            }
        }
        if (bitmap != null) {
            viewModel.updateBitmap(bitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        CropPanelFragment.cropListener = this
        ImagePickerFragment.pickerListener = this
        MainPanelFragment.mainPanelListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        CropPanelFragment.cropListener = null
        ImagePickerFragment.pickerListener = null
        MainPanelFragment.mainPanelListener = null
    }

    override fun cropOvalClick() {
        image_preview_editor.setOnTouchListener(null)
        image_preview_editor.cropOval()
        image_preview_editor.saveCrop()
    }

    override fun cropRectangleClick() {
        image_preview_editor.setOnTouchListener(null)
        image_preview_editor.cropRectangle()
        image_preview_editor.saveCrop()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun cropCustomClick() {
        image_preview_editor.cropCustom()
        image_preview_editor.setOnTouchListener { v, event ->
            if (event.action == ACTION_MOVE) {
                image_preview_editor.updateCropCustom(event.x, event.y)
            }
            true
        }
    }

    override fun resetCropClick() {
        image_preview_editor.setOnTouchListener(null)
        image_preview_editor.resetCrop()
    }

    override fun saveCropClick() {
        image_preview_editor.setOnTouchListener(null)
        image_preview_editor.saveCrop()
    }

    override fun selectImageListener(bitmap: Bitmap) {
        image_preview_editor.setImageBitmap(bitmap)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_REQUEST_CODE -> if (grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                if (image_preview_editor.mViewBitmap != null) {
                    ImagePickerManager.saveImageToGallery(requireContext(), image_preview_editor.mViewBitmap!!)
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

    override fun rotateLeftClick() {
        image_preview_editor.rotate(CustomImageView.ROTATE_LEFT)
    }

    override fun rotateRightClick() {
        image_preview_editor.rotate(CustomImageView.ROTATE_RIGHT)
    }

    override fun flipClick() {
        image_preview_editor.flip()
    }

    private fun initObservers() {
        val b = Observer<Bitmap> { b ->
            image_preview_editor.setImageBitmap(b)
        }
        viewModel.bitmap.observe(viewLifecycleOwner, b)
    }

    private fun showBottomSheetDialog() {
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

    private fun initBottomSheet(activity: Activity) {

        /* Init bottom sheet dialog. */
        dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
        dialog = BottomSheetDialog(activity)
        dialog.setContentView(dialogView)
    }
}