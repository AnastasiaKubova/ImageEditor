package com.example.imageeditor.utility

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.imageeditor.R
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


object ImageManager {

    private val SELECT_TYPE = "image/*"
    private var currentRotate = 0f
    private var isFlipped = false
    private var tempCropImage: Bitmap? = null

    var currentBitmap: Bitmap? = null
    val REQUEST_IMAGE_CAPTURE = 1
    val PICK_IMAGE = 2
    val ROTATE_LEFT = -90f
    val ROTATE_RIGHT = 90f

    fun loadImageFromCamera(fragment: Fragment) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(fragment.requireActivity().packageManager)
            fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun saveFileToGallery(context: Context, uri: String) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(uri)
            mediaScanIntent.data = Uri.fromFile(f)
            context.sendBroadcast(mediaScanIntent)
        }
    }

    fun loadFromGallery(fragment: Fragment) {

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

    fun createBitmap(path: String): Bitmap? {
        return BitmapFactory.decodeFile(path)
    }

    fun flipImage(): Bitmap? {
        if (currentBitmap == null) {
            return null
        }
        val matrix = Matrix()
        if (isFlipped) {
            matrix.preScale(1.0f, 1.0f)
        } else {
            matrix.preScale(-1.0f, 1.0f)
        }
        isFlipped = !isFlipped
        currentBitmap = Bitmap.createBitmap(currentBitmap!!, 0, 0, currentBitmap!!.width, currentBitmap!!.height, matrix, false)
        return currentBitmap
    }

    fun rotateImage(angle: Float): Bitmap? {
        if (currentBitmap == null) {
            return null
        }
        when(currentRotate) {
            0f -> {
                if (angle > 0) {
                    currentRotate += angle
                } else {
                    currentRotate = 270f
                }
            }
            270f -> {
                if (angle > 0) {
                    currentRotate = 0f
                } else {
                    currentRotate += angle
                }
            }
            else -> {
                currentRotate += angle
            }
        }
        val matrix = Matrix()
        matrix.preRotate(currentRotate)
        currentBitmap = Bitmap.createBitmap(currentBitmap!!,0,0, currentBitmap!!.width, currentBitmap!!.height, matrix, true)
        return currentBitmap
    }

    fun cropRectangle(): Bitmap?  {
        if (currentBitmap == null) {
            return null
        }
        tempCropImage = Bitmap.createBitmap(currentBitmap!!)
        tempCropImage =  ThumbnailUtils.extractThumbnail(tempCropImage, tempCropImage!!.width, tempCropImage!!.width)
        return tempCropImage
    }

    fun cropOval(): Bitmap? {
        if (currentBitmap == null) {
            return null
        }
        tempCropImage = Bitmap.createBitmap(currentBitmap!!)
        val width = tempCropImage!!.width / 2
        val height = tempCropImage!!.height / 2

        /* Create empty bitmap. */
        val output = Bitmap.createBitmap(tempCropImage!!.width, tempCropImage!!.height, Bitmap.Config.ARGB_8888)

        /* Draw empty bitmap in canvas. */
        val canvas = Canvas(output)

        /* Prepare paint for draw template. */
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val path = Path()
        val radius = if (width > height) {
            height
        } else {
            width
        }
        path.addCircle(width.toFloat(), height.toFloat(), radius.toFloat(), Path.Direction.CW)
        paint.color = -0x1000000
        canvas.drawPath(path, paint)

        /**
         *
         * More about flags here https://developer.android.com/reference/android/graphics/PorterDuff.Mode.html#SRC_IN.
         */
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        /* Draw image for cut. */
        canvas.drawBitmap(tempCropImage!!, 0f, 0f, paint)
        tempCropImage = output
        return tempCropImage!!
    }

    fun saveCrop(): Bitmap? {
        if (tempCropImage != null) {
            currentBitmap = tempCropImage
            tempCropImage = null
        }
        return currentBitmap
    }

    fun resetCrop(): Bitmap? {
        tempCropImage = null
        return currentBitmap
    }
}