package com.example.imageeditor.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.imageeditor.R
import java.io.File
import java.io.InputStream


object ImageManager {

    private const val SELECT_TYPE = "image/*"
    private const val DELTA = 70f

    private var tempCropImage: Bitmap? = null
    private var rectLeft: RectF? = null
    private var rectRight: RectF? = null
    private var rectTop: RectF? = null
    private var rectBottom: RectF? = null

    const val DEFAULT_PADDING = 50f
    const val REQUEST_IMAGE_CAPTURE = 1
    const val PICK_IMAGE = 2
    const val ROTATE_LEFT = -90f
    const val ROTATE_RIGHT = 90f

    var currentBitmap: Bitmap? = null
    
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
        matrix.preScale(-1.0f, 1.0f)
        currentBitmap = Bitmap.createBitmap(currentBitmap!!, 0, 0, currentBitmap!!.width, currentBitmap!!.height, matrix, false)
        return currentBitmap
    }

    fun rotateImage(angle: Float): Bitmap? {
        if (currentBitmap == null) {
            return null
        }
        val matrix = Matrix()
        matrix.postRotate(angle)
        currentBitmap = Bitmap.createBitmap(currentBitmap!!,0,0, currentBitmap!!.width, currentBitmap!!.height, matrix, true)
        return currentBitmap
    }

    fun cropRectangle(): Bitmap?  {
        if (currentBitmap == null) {
            return null
        }
        tempCropImage =  ThumbnailUtils.extractThumbnail(currentBitmap, currentBitmap!!.width, currentBitmap!!.width)
        return tempCropImage
    }

    fun cropOval(): Bitmap? {
        if (currentBitmap == null) {
            return null
        }
        val width = currentBitmap!!.width / 2
        val height = currentBitmap!!.height / 2

        /* Create empty bitmap. */
        val output = Bitmap.createBitmap(currentBitmap!!.width, currentBitmap!!.height, Bitmap.Config.ARGB_8888)

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
        canvas.drawBitmap(currentBitmap!!, 0f, 0f, paint)
        tempCropImage = output
        return output
    }

    @SuppressLint("ResourceAsColor")
    fun cropCustom(x: Float, y: Float):  Bitmap? {
        if (currentBitmap == null) {
            return null
        }

        /* Init bitmap. */
        val width = currentBitmap!!.width
        val height = currentBitmap!!.height

        /* Init rect coordinates. */
        if (rectBottom == null || rectLeft == null || rectRight == null || rectTop == null) {
            initRectCoords(width.toFloat(), height.toFloat())
        }

        /* Create empty bitmap. */
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        /* Draw empty bitmap in canvas. */
        val canvas = Canvas(output)
        canvas.drawBitmap(currentBitmap!!, 0f, 0f, Paint())

        /* Prepare paint for draw template. */
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val path = Path()
        paint.color = R.color.colorPrimaryTransparent

        /* Update rectangle coorgs. */
        updateRect(x, y)

        /* Left rectangle. */
        path.addRect(rectLeft!!, Path.Direction.CW)

        /* Right rectangle. */
        path.addRect(rectRight!!, Path.Direction.CW)

        /* Top rectangle. */
        path.addRect(rectTop!!, Path.Direction.CW)

        /* Bottom rectangle. */
        path.addRect(rectBottom!!, Path.Direction.CW)

        /* Draw drawable. */
        canvas.drawPath(path, paint)

        /* Draw image for cut. */
        canvas.drawBitmap(output!!, 0f, 0f, paint)
        tempCropImage = saveCustomCrop()
        return output
    }

    fun saveCrop(): Bitmap? {
        if (tempCropImage != null) {
            currentBitmap = tempCropImage
            tempCropImage = null
        }
        resetRectCoords()
        return currentBitmap
    }

    fun resetCrop(): Bitmap? {
        tempCropImage = null
        resetRectCoords()
        return currentBitmap
    }

    private fun saveCustomCrop(): Bitmap? {
        if (currentBitmap == null) {
            return null
        }

        /* Create empty bitmap. */
        val output = Bitmap.createBitmap(currentBitmap!!.width, currentBitmap!!.height, Bitmap.Config.ARGB_8888)

        /* Draw empty bitmap in canvas. */
        val canvas = Canvas(output)

        /* Prepare paint for draw template. */
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        /* Draw rectangles. */
        val path = Path()
        paint.color = -0x1000000
        path.addRect(rectLeft!!.right, rectTop!!.bottom, rectRight!!.left, rectBottom!!.top, Path.Direction.CW)
        canvas.drawPath(path, paint)

        /* Draw final image. */
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(currentBitmap!!, 0f, 0f, paint)
        return output
    }

    private fun updateRect(x: Float, y: Float) {

        /* Top right. */
        if (rectTop!!.bottom + DELTA > y && rectRight!!.left + DELTA > x
            && rectTop!!.bottom - DELTA < y && rectRight!!.left - DELTA < x) {
            rectTop!!.bottom = y
            rectRight!!.left = x
        }
        /* Top left. */
        else if (rectTop!!.bottom + DELTA > y && rectLeft!!.right + DELTA > x
            && rectTop!!.bottom - DELTA < y && rectLeft!!.right - DELTA < x) {
            rectTop!!.bottom = y
            rectLeft!!.right = x
        }
        /* Bottom right. */
        else if (rectBottom!!.top - DELTA > y && rectRight!!.left - DELTA > x
            && rectBottom!!.top + DELTA < y && rectRight!!.left + DELTA < x) {
            rectBottom!!.top = y
            rectRight!!.left = x
        }
        /* Bottom left. */
        else if (rectBottom!!.top + DELTA > y && rectLeft!!.right + DELTA > x
            && rectBottom!!.top - DELTA < y && rectLeft!!.right - DELTA < x) {
            rectBottom!!.top = y
            rectLeft!!.right = x
        }
    }

    private fun initRectCoords(width: Float, height: Float) {
        val deltaX = width * 0.1f
        val deltaY = height * 0.1f
        rectLeft = RectF(0f, 0f, deltaX, height)
        rectRight = RectF(width - deltaX, 0f, width, height)
        rectTop = RectF(0f, 0f, width, deltaY)
        rectBottom = RectF(0f, height - deltaY, width, height)
    }

    private fun resetRectCoords() {
        rectBottom = null
        rectLeft = null
        rectRight = null
        rectTop = null
    }
}