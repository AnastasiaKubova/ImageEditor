package com.example.imageeditor.ui.imagepicker

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.R
import com.example.imageeditor.model.FileItem
import com.example.imageeditor.ui.BaseFragment
import com.example.imageeditor.ui.MainActivity
import com.example.imageeditor.ui.croppanel.CropPanelListener
import com.example.imageeditor.ui.editor.ImageEditorViewModel
import com.example.imageeditor.utility.DrawManager
import com.example.imageeditor.utility.ImagePickerManager


class ImagePickerFragment: BaseFragment(), ImagePickerAdapter.FileItemListener, MainActivity.MainActivityListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: GridLayoutManager

    companion object {
        val instance = ImagePickerFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.image_picker_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Init menu. */
        setHasOptionsMenu(true)
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)

        /* Init item list. */
        initImageList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.image_picker_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initImageList() {
        val numberOfColumns = 4
        viewManager = GridLayoutManager(requireContext(), numberOfColumns)
        viewAdapter = ImagePickerAdapter(ImagePickerManager.loadPreviewImagesList(true), this)
        recyclerView = requireActivity().findViewById<RecyclerView>(R.id.image_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.addItemDecoration(SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.dimension_decorator)))
    }

    override fun onFolderClick(folder: FileItem) {
        updateList(ImagePickerManager.loadImagesByFile(folder.file, true))
    }

    override fun onImageClick(image: FileItem) {
        DrawManager.currentBitmap = ImagePickerManager.createBitmap(image.file)
        findNavController().navigateUp()
    }

    override fun onResume() {
        super.onResume()
        MainActivity.listener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        MainActivity.listener = null
        ImagePickerManager.fileStack.empty()
    }

    override fun onBackPressed() {
        val list = ImagePickerManager.loadPreviousFile()
        if (list.count() == 0) {
            findNavController().popBackStack()
            findNavController().navigate(R.id.imageEditorFragment)
        } else {
            updateList(list)
        }
    }

    private fun updateList(list: MutableList<FileItem>) {
        (viewAdapter as ImagePickerAdapter).imageList = list
        viewAdapter.notifyDataSetChanged()
    }
}