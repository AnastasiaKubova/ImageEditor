package com.example.imageeditor.ui.editor

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imageeditor.utility.DrawManager
import com.example.imageeditor.utility.ImagePickerManager
import java.io.File

class ImageEditorViewModel: ViewModel() {

    val bitmap: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    fun initBitmap() {
        if (DrawManager.currentBitmap != null) {
            bitmap.value = DrawManager.currentBitmap
        }
    }

    fun setBitmap(btmp: Bitmap) {
        DrawManager.currentBitmap = btmp
        bitmap.value = btmp
    }

    fun rotateLeft() {
        bitmap.value = DrawManager.rotateImage(ImagePickerManager.ROTATE_LEFT)
    }

    fun rotateRight() {
        bitmap.value = DrawManager.rotateImage(ImagePickerManager.ROTATE_RIGHT)
    }

    fun flip() {
        bitmap.value = DrawManager.flipImage()
    }

    fun cropOval() {
        bitmap.value = DrawManager.cropOval()
    }

    fun cropRectangle() {
        bitmap.value = DrawManager.cropRectangle()
    }

    fun cropCustom(x: Float, y: Float) {
        bitmap.value = DrawManager.cropCustom(x, y)
    }

    fun resetCrop() {
        bitmap.value = DrawManager.resetCrop()
    }

    fun saveCrop() {
        bitmap.value = DrawManager.saveCrop()
    }
}