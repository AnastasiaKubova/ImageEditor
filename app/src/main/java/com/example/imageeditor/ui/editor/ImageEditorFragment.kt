package com.example.imageeditor.ui.editor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.imageeditor.R
import com.example.imageeditor.ui.BaseFragment
import com.example.imageeditor.utility.ImageManager
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.image_editot_fragment.*
import kotlinx.android.synthetic.main.template_panel.*


class ImageEditorFragment: BaseFragment() {

    companion object {
        val instance = ImageEditorFragment()
    }

    private val viewModel by viewModels<ImageEditorViewModel>()
    private var isCropPanelOpen = false

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

        /* Init click listeners. */
        rotate_left_bottom_menu.setOnClickListener { rotateLeftClick() }
        rotate_right_bottom_menu.setOnClickListener { rotateRightClick() }
        resize_bottom_menu.setOnClickListener { resizeClick() }
        flip_bottom_menu.setOnClickListener { flipClick() }
        template_oval.setOnClickListener { resizeOvalClick() }
        template_rectangle.setOnClickListener { resizeRectangleClick() }
        template_reset.setOnClickListener { resetResizeClick() }
        template_custom.setOnClickListener { resizeCustomClick(ImageManager.DEFAULT_PADDING, ImageManager.DEFAULT_PADDING) }

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
        if (requestCode == ImageManager.REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data.extras == null) {
                return
            }
            bitmap = data.extras!!.get("data") as Bitmap
        }
        /* From default picker. */
        else if (requestCode == ImageManager.PICK_IMAGE) {
            if (data.data == null) {
                return
            }
            val imageStream = requireActivity().contentResolver.openInputStream(data.data!!)
            if (imageStream != null) {
                bitmap = ImageManager.createBitmap(imageStream)
            }
        }
        if (bitmap != null) {
            viewModel.initBitmap(bitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initObservers() {
        val bitmap = Observer<Bitmap> { bitmap ->
            image_preview_editor.setImageBitmap(bitmap)
        }
        viewModel.bitmap.observe(requireActivity(), bitmap)
    }

    private fun rotateLeftClick() {
        viewModel.rotateLeft()
        showTemplatePanel(View.GONE, true)
    }

    private fun rotateRightClick() {
        viewModel.rotateRight()
        showTemplatePanel(View.GONE, true)
    }

    private fun resetResizeClick() {
        showTemplatePanel(View.GONE, false)
    }

    private fun flipClick() {
        viewModel.flip()
        showTemplatePanel(View.GONE, true)
    }

    private fun resizeClick() {
        showTemplatePanel(if (isCropPanelOpen) View.GONE else View.VISIBLE, isCropPanelOpen)
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

    private fun showTemplatePanel(vis: Int, save: Boolean) {
        template_panel.visibility = vis
        when(vis) {
            View.GONE -> {
                isCropPanelOpen = false
                image_preview_editor.setOnTouchListener(null)
            }
            View.VISIBLE -> {
                isCropPanelOpen = true
                image_preview_editor.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_MOVE) {
                        viewModel.cropCustom(event.x, event.y)
                    }
                    true
                }
            }
        }
        if (save) {
            viewModel.saveCrop()
        } else {
            viewModel.resetCrop()
        }
    }
}