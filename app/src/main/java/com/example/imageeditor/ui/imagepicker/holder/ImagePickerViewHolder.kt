package com.example.imageeditor.ui.imagepicker.holder

import android.app.AlertDialog
import android.app.Dialog
import android.view.MotionEvent.ACTION_MOVE
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.imageeditor.R
import com.example.imageeditor.model.FileItem
import com.example.imageeditor.ui.imagepicker.ImagePickerAdapter
import com.example.imageeditor.utility.ImagePickerManager
import kotlinx.android.synthetic.main.image_item.view.*
import java.io.File

class ImagePickerViewHolder(itemView: View, var listener: ImagePickerAdapter.FileItemListener) : RecyclerView.ViewHolder(itemView), BaseHolder {

    override fun bind(image: FileItem) {
        Glide.with(itemView.context).load(image.file).centerCrop().into(itemView.image_item)
        itemView.setOnClickListener { listener.onImageClick(image) }
        itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle(image.name)
            val view = View.inflate(itemView.context, R.layout.dialog_picker, null)
            val imageView = view.findViewById<ImageView>(R.id.dialog_image)
            Glide.with(itemView.context).load(image.file).centerCrop().into(imageView)
            builder.setView(view)
            builder.show()
            true
        }
    }
}