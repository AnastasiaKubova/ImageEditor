package com.example.imageeditor.ui.imagepicker

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.R
import com.example.imageeditor.model.FileItem
import com.example.imageeditor.ui.BaseFragment
import com.example.imageeditor.ui.MainActivity
import com.example.imageeditor.utility.ImagePickerManager


class ImagePickerFragment: BaseFragment(), ImagePickerAdapter.FileItemListener, MainActivity.MainActivityListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: GridLayoutManager

    companion object {
        val instance = ImagePickerFragment()
        var pickerListener: ImagePickerListener? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.image_picker_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /* Init menu. */
        changeFragmentListener?.onChangeFragment(true)

        /* Init item list. */
        initImageList()
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
        val b = ImagePickerManager.createBitmap(image.file)
        if (b != null) {
            pickerListener?.selectImageListener(b)
        }
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

    interface ImagePickerListener {
        fun selectImageListener(bitmap: Bitmap)
    }
}