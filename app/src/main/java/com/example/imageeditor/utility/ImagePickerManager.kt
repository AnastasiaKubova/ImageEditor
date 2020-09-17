package com.example.imageeditor.utility

import android.R.attr.description
import android.R.attr.thumb
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.imageeditor.R
import com.example.imageeditor.enum.FileItemType
import com.example.imageeditor.model.FileItem
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.*


object ImagePickerManager {

    private const val LOG_TAG = "ImagePickerManager"
    private const val SELECT_TYPE = "image/*"
    private const val IMAGE_EXTENSION = "jpg png"

    val fileStack: Stack<File> = Stack<File>()

    const val DEFAULT_PADDING = 50f
    const val REQUEST_IMAGE_CAPTURE = 1
    const val PICK_IMAGE = 2
    const val ROTATE_LEFT = -90f
    const val ROTATE_RIGHT = 90f

    fun loadImageFromCamera(fragment: Fragment) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(fragment.requireActivity().packageManager)
            fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun saveImageToGallery(context: Context, bitmap: Bitmap) {

        /* Create image name. */
        val title = System.currentTimeMillis().toString()

        /* Init content values. */
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        values.put(MediaStore.Images.Media.DESCRIPTION, description)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, R.string.folder_name)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
        }

        /* Get uri to file. */
        val url = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return
        try {

            /* Wright bitmap to file. */
            val file = url.let { context.contentResolver.openOutputStream(it) }
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, file)
            file?.close()

            /* Update content value. */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.IS_PENDING, false)
            }
            context.contentResolver.update(url, values, null, null)
        } catch (ex: FileNotFoundException) {
            Log.d(LOG_TAG, "Error during save.")
        } catch (ex: IOException) {
            Log.d(LOG_TAG, "Error during save.")
        }
    }

    fun loadImagesFromGallery(fragment: Fragment) {

        /* Get from files folder. */
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = SELECT_TYPE

        /* Get from photo. */
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pickIntent.type = SELECT_TYPE

        /* Start activity. */
        val chooserIntent = Intent.createChooser(getIntent, fragment.getString(R.string.select_picture))
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        fragment.startActivityForResult(chooserIntent, PICK_IMAGE)
    }

    fun createBitmap(stream: InputStream): Bitmap? {
        return BitmapFactory.decodeStream(stream)
    }

    fun createBitmap(file: File): Bitmap? {
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    fun loadPreviewImagesList(saveToTack: Boolean) : MutableList<FileItem> {
        return loadImagesByFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), saveToTack)
    }

    fun loadPreviousFile(): MutableList<FileItem> {
        if (fileStack.isEmpty()) {
            return mutableListOf()
        }
        /* Remove current file. */
        fileStack.pop()

        /* Check that current stack is still not empty. */
        if (fileStack.isEmpty()) {
            return mutableListOf()
        }

        /* And get previous. */
        val file = fileStack.pop()
        return getFiles(file)
    }

    fun loadImagesByFile(file: File, saveToTack: Boolean): MutableList<FileItem> {
        if (saveToTack) {
            fileStack.push(file)
        }
        return getFiles(file)
    }

    private fun getFiles(file: File): MutableList<FileItem> {

        /* Get files from DCIM folder. */
        // WHY this is not work o_O context.getExternalFilesDir(Environment.DIRECTORY_DCIM) ????
        if (!file.isDirectory) {
            return mutableListOf()
        }
        val images = file.listFiles().filter {
            it.isDirectory || IMAGE_EXTENSION.contains(it.extension)
        }.map {
            FileItem(
                it.name, Date(it.lastModified()), it,
                if (it.isDirectory) FileItemType.FOLDER else FileItemType.IMAGE
            )
        }
        return images.toMutableList()
    }
}