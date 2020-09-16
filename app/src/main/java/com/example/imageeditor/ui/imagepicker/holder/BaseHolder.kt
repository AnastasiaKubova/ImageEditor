package com.example.imageeditor.ui.imagepicker.holder

import com.example.imageeditor.model.FileItem

interface BaseHolder {
    fun bind(image: FileItem)
}