package com.example.imageeditor.ui.imagepicker

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.R
import com.example.imageeditor.enum.FileItemType
import com.example.imageeditor.model.FileItem
import com.example.imageeditor.ui.imagepicker.holder.BaseHolder
import com.example.imageeditor.ui.imagepicker.holder.FolderViewHolder
import com.example.imageeditor.ui.imagepicker.holder.HeaderViewHolder
import com.example.imageeditor.ui.imagepicker.holder.ImagePickerViewHolder

class ImagePickerAdapter(var imageList: MutableList<FileItem>, var listener: FileItemListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FileItemType.IMAGE.ordinal -> ImagePickerViewHolder(View.inflate(parent.context, R.layout.image_item, null), listener)
            FileItemType.FOLDER.ordinal -> FolderViewHolder(View.inflate(parent.context, R.layout.folder_item, null), listener)
            else -> HeaderViewHolder(View.inflate(parent.context, R.layout.image_separator_item, null))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return imageList[position].type.ordinal
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseHolder).bind(imageList[position])
    }

    interface FileItemListener {
        fun onFolderClick(folder: FileItem)
        fun onImageClick(image: FileItem)
    }
}