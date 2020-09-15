package com.example.imageeditor.ui.editor

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.imageeditor.utility.ImageManager

class ImageEditorViewModel: ViewModel() {

    val bitmap: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    fun initBitmap(btmp: Bitmap) {
        ImageManager.currentBitmap = btmp
        bitmap.value = btmp
    }

    fun rotateLeft() {
        bitmap.value = ImageManager.rotateImage(ImageManager.ROTATE_LEFT)
    }

    fun rotateRight() {
        bitmap.value = ImageManager.rotateImage(ImageManager.ROTATE_RIGHT)
    }

    fun flip() {
        bitmap.value = ImageManager.flipImage()
    }

    fun cropOval() {
        bitmap.value = ImageManager.cropOval()
    }

    fun cropRectangle() {
        bitmap.value = ImageManager.cropRectangle()
    }

    fun resetCrop() {
        bitmap.value = ImageManager.resetCrop()
    }

    fun saveCrop() {
        bitmap.value = ImageManager.saveCrop()
    }
}