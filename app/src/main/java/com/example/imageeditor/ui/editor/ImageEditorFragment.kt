package com.example.imageeditor.ui.editor

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.MotionEvent.ACTION_MOVE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.imageeditor.R
import com.example.imageeditor.ui.BaseFragment
import com.example.imageeditor.ui.croppanel.CropPanelListener
import com.example.imageeditor.utility.DrawManager
import com.example.imageeditor.utility.ImagePickerManager
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.image_editot_fragment.*
import kotlinx.android.synthetic.main.crop_panel.*


class ImageEditorFragment: BaseFragment(), CropPanelListener {

    companion object {
        val instance = ImageEditorFragment()
    }

    private val viewModel by viewModels<ImageEditorViewModel>()

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

        /* Init Observers. */
        initObservers()

        /* Init bottom sheet. */
        initBottomSheet(requireActivity())
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
            viewModel.setBitmap(bitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        viewModel.initBitmap()
        cropListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        cropListener = null
    }

    override fun onCropStart() {
        image_preview_editor.setOnTouchListener { v, event ->
            if (event.action == ACTION_MOVE) {
                DrawManager.cropCustom(event.x, event.y)
            }
            true
        }
    }

    override fun onCropStop() {
        image_preview_editor.setOnTouchListener(null)
    }

    private fun initObservers() {
        val bitmap = Observer<Bitmap> { bitmap ->
            image_preview_editor.setImageBitmap(bitmap)
        }
        viewModel.bitmap.observe(viewLifecycleOwner, bitmap)
    }
}