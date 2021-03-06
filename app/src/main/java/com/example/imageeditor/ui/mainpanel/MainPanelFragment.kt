package com.example.imageeditor.ui.mainpanel

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.imageeditor.R
import com.example.imageeditor.ui.BaseFragment
import com.example.imageeditor.ui.editor.ImageEditorViewModel
import com.example.imageeditor.utility.ImagePickerManager
import kotlinx.android.synthetic.main.bottom_menu.*
import kotlinx.android.synthetic.main.image_editot_fragment.*
import kotlinx.android.synthetic.main.crop_panel.*

class MainPanelFragment: BaseFragment() {

    companion object {
        val instance = MainPanelFragment()
        var mainPanelListener: MenuPanelListener? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Init click listeners. */
        rotate_left_bottom_menu.setOnClickListener { mainPanelListener?.rotateLeftClick() }
        rotate_right_bottom_menu.setOnClickListener { mainPanelListener?.rotateRightClick() }
        resize_bottom_menu.setOnClickListener { resizeClick() }
        flip_bottom_menu.setOnClickListener { mainPanelListener?.flipClick() }
    }

    private fun resizeClick() {
        openCropPanel()
    }

    interface MenuPanelListener {
        fun rotateLeftClick()
        fun rotateRightClick()
        fun flipClick()
    }
}