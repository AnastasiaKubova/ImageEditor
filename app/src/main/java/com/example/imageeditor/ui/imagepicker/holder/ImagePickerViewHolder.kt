package com.example.imageeditor.ui.imagepicker.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imageeditor.model.FileItem
import com.example.imageeditor.ui.imagepicker.ImagePickerAdapter
import com.example.imageeditor.utility.ImagePickerManager
import kotlinx.android.synthetic.main.image_item.view.*
import java.io.File

class ImagePickerViewHolder(itemView: View, var listener: ImagePickerAdapter.FileItemListener) : RecyclerView.ViewHolder(itemView), BaseHolder {

    override fun bind(image: FileItem) {
        Glide.with(itemView.context).load(image.file).centerCrop().into(itemView.image_item)
        itemView.setOnClickListener { listener?.onImageClick(image) }
    }
}