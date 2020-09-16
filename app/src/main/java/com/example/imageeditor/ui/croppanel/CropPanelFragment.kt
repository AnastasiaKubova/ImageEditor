package com.example.imageeditor.ui.croppanel

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.imageeditor.R
import com.example.imageeditor.ui.BaseFragment
import com.example.imageeditor.ui.editor.ImageEditorViewModel
import com.example.imageeditor.utility.ImagePickerManager
import kotlinx.android.synthetic.main.image_editot_fragment.*
import kotlinx.android.synthetic.main.crop_panel.*

class CropPanelFragment: BaseFragment() {

    companion object {
        val instance = CropPanelFragment()
    }

    private val viewModel: ImageEditorViewModel by viewModels(ownerProducer = { requireParentFragment().requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.crop_panel, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Init menu. */
        setHasOptionsMenu(true)

        /* Init listeners. */
        template_oval.setOnClickListener { resizeOvalClick() }
        template_rectangle.setOnClickListener { resizeRectangleClick() }
        template_custom.setOnClickListener { resizeCustomClick(ImagePickerManager.DEFAULT_PADDING, ImagePickerManager.DEFAULT_PADDING) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.crop_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.check_menu -> {
                viewModel.saveCrop()
                openMenuPanel()
                true
            }
            R.id.reset_menu -> {
                resetResetClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resizeOvalClick() {
        viewModel.cropOval()
    }

    private fun resizeRectangleClick() {
        viewModel.cropRectangle()
    }

    private fun resizeCustomClick(x: Float, y: Float) {
        viewModel.cropCustom(x, y)
    }

    private fun resetResetClick() {
        viewModel.resetCrop()
        openMenuPanel()
    }
}