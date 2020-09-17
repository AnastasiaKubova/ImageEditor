package com.example.imageeditor.ui.imagepicker.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.imageeditor.model.FileItem
import com.example.imageeditor.ui.imagepicker.ImagePickerAdapter
import kotlinx.android.synthetic.main.folder_item.view.*

class FolderViewHolder(itemView: View, var listener: ImagePickerAdapter.FileItemListener) : RecyclerView.ViewHolder(itemView), BaseHolder {

    override fun bind(folder: FileItem) {
        itemView.folder_name_item.text = folder.name
        itemView.setOnClickListener { listener?.onFolderClick(folder) }
    }
}