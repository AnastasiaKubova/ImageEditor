package com.example.imageeditor.ui.editor

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageEditorViewModel: ViewModel() {

    val bitmap: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    fun updateBitmap(btmp: Bitmap) {
        bitmap.value = btmp
    }
}