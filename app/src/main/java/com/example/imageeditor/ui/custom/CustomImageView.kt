package com.example.imageeditor.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.ThumbnailUtils
import android.util.AttributeSet
import android.view.View
import com.example.imageeditor.R

class CustomImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {
        val DELTA = 170f
        val ROTATE_LEFT = -90f
        val ROTATE_RIGHT = 90f
    }

    var mViewBitmap: Bitmap? = null
        private set

    private var mTempCropImage: Bitmap? = null
    private var rectLeft: RectF? = null
    private var rectRight: RectF? = null
    private var rectTop: RectF? = null
    private var rectBottom: RectF? = null
    private var isCustomCropMode: Boolean = false

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mViewBitmap == null) {
            return
        }
        if (isCustomCropMode) {
            onDrawCropCustomMode(canvas)
        }
        else {
            canvas?.drawBitmap(mViewBitmap!!, width / 2f - mViewBitmap!!.width / 2f, height / 2f - mViewBitmap!!.height / 2f, null)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun onDrawCropCustomMode(canvas: Canvas?) {

        /* Draw empty bitmap in canvas. */
        val left = width / 2f - mViewBitmap!!.width / 2f
        val top = height / 2f - mViewBitmap!!.height / 2f
        canvas?.drawBitmap(mViewBitmap!!, left, top, Paint())

        /* Prepare paint for draw template. */
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val path = Path()
        paint.color = R.color.colorPrimaryTransparent

        /* Left rectangle. */
        path.addRect(rectLeft!!, Path.Direction.CW)

        /* Right rectangle. */
        path.addRect(rectRight!!, Path.Direction.CW)

        /* Top rectangle. */
        path.addRect(rectTop!!, Path.Direction.CW)

        /* Bottom rectangle. */
        path.addRect(rectBottom!!, Path.Direction.CW)

        /* Draw drawable. */
        canvas?.drawPath(path, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resizeBitmap()
    }

    fun setImageBitmap(bitmap: Bitmap) {
        mViewBitmap = bitmap
        mTempCropImage = null
        if (width != 0) {
            resizeBitmap()
        }
        invalidate()
    }

    private fun setTempBitmap(bitmap: Bitmap) {
        mViewBitmap = bitmap
        mTempCropImage = bitmap
        if (width != 0) {
            resizeBitmap()
        }
        invalidate()
    }

    fun flip() {
        if (mViewBitmap == null) {
            return
        }
        val b = initFlipImage(mViewBitmap!!)
        setImageBitmap(b)
    }

    fun rotate(angle: Float) {
        if (mViewBitmap == null) {
            return
        }
        val b = initRotateImage(angle, mViewBitmap!!)
        setImageBitmap(b)
    }

    fun cropRectangle() {
        if (mViewBitmap == null) {
            return
        }
        isCustomCropMode = false
        val b = prepareCropRectangle(mViewBitmap!!)
        if (b != null) {
            setTempBitmap(b)
        }
    }

    fun cropOval() {
        if (mViewBitmap == null) {
            return
        }
        isCustomCropMode = false
        val b = prepareCropOval(mViewBitmap!!)
        if (b != null) {
            setTempBitmap(b)
        }
    }

    fun cropCustom() {
        if (mViewBitmap == null) {
            return
        }
        isCustomCropMode = true
        setTempBitmap(mViewBitmap!!)

        /* Init bitmap size. */
        val bWidth = mViewBitmap!!.width.toFloat()
        val bHeight = mViewBitmap!!.height.toFloat()

        /* Init rect coordinates. */
        if (rectBottom == null || rectLeft == null || rectRight == null || rectTop == null) {
            initRectCoords(bWidth, bHeight)
        }
    }

    fun updateCropCustom(x: Float, y: Float) {
        if (mViewBitmap == null || !isCustomCropMode) {
            return
        }

        /* Update rectangle coorgs. */
        updateRect(x, y)
        setTempBitmap(mViewBitmap!!)
    }

    fun saveCrop() {
        if (mViewBitmap != null) {
            if (isCustomCropMode) {
                mTempCropImage = saveCustomCrop(mViewBitmap!!)
                isCustomCropMode = false
            }
            setImageBitmap(mTempCropImage!!)
        }
        resetRectCoords()
    }

    fun resetCrop() {
        mTempCropImage = null
        isCustomCropMode = false
        resetRectCoords()
        if (mViewBitmap != null) {
            setImageBitmap(mViewBitmap!!)
        }
    }

    private fun initFlipImage(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
    }

    private fun initRotateImage(angle: Float, bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(bitmap,0,0, bitmap.width, bitmap.height, matrix, false)
    }

    private fun prepareCropRectangle(bitmap: Bitmap): Bitmap?  {
        val b = ThumbnailUtils.extractThumbnail(bitmap, bitmap.width, bitmap.width)
        return b
    }

    private fun prepareCropOval(bitmap: Bitmap): Bitmap? {
        val width = bitmap.width
        val height = bitmap.height
        var topCoord = 0f
        var leftCoord = 0f
        var size = 0f
        if (width > height) {
            size = height.toFloat()
            leftCoord -= size / 2
        } else {
            size = width.toFloat()
            topCoord -= size / 2
        }

        /* Create empty bitmap. */
        val output = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)

        /* Draw empty bitmap in canvas. */
        val canvas = Canvas(output)

        /* Prepare paint for draw template. */
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val path = Path()

        path.addCircle(size / 2, size / 2, size / 2, Path.Direction.CW)
        paint.color = -0x1000000
        canvas.drawPath(path, paint)

        /**
         *
         * More about flags here https://developer.android.com/reference/android/graphics/PorterDuff.Mode.html#SRC_IN.
         */
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        /* Draw image for cut. */
        canvas.drawBitmap(bitmap, leftCoord, topCoord, paint)
        return output
    }

    private fun resizeBitmap() {

        // Resize bitmap for show image in display.
        val bitmap = mViewBitmap ?: return
        val h = width.toFloat() * bitmap.height / bitmap.width
        val w = height.toFloat()  * bitmap.width / bitmap.height
        mViewBitmap = if (h < height) {
            Bitmap.createScaledBitmap(bitmap, width, h.toInt(),true)
        } else {
            Bitmap.createScaledBitmap(bitmap, w.toInt(), height,true)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun saveCustomCrop(bitmap: Bitmap): Bitmap? {
        val newWidth = rectRight!!.left - rectLeft!!.right
        val newHeight = rectBottom!!.top - rectTop!!.bottom

        /* Create empty bitmap. */
        val output = Bitmap.createBitmap(newWidth.toInt(), newHeight.toInt(), Bitmap.Config.ARGB_8888)

        /* Draw empty bitmap in canvas. */
        val canvas = Canvas(output)

        /* Prepare paint for draw template. */
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(bitmap, - rectLeft!!.width(), - rectTop!!.height(), paint)
        resetRectCoords()
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
        else if (rectBottom!!.top + DELTA > y && rectRight!!.left + DELTA > x
            && rectBottom!!.top - DELTA < y && rectRight!!.left - DELTA < x) {
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

    private fun initRectCoords(wb: Float, hb: Float) {
        var delta = 0f
        delta = if (width > height) {
            height * 0.1f
        } else {
            width * 0.1f
        }
        val left = width / 2f - wb / 2f
        val top = height / 2f - hb / 2f
        val right =  width / 2f + wb / 2f
        val bottom = height / 2f + hb / 2f
        rectLeft = RectF(left, top, left + delta, bottom)
        rectTop = RectF(left, top, right, top + delta)
        rectRight = RectF(right - delta, top, right, bottom)
        rectBottom = RectF(left, bottom - delta, right, bottom)
    }

    private fun resetRectCoords() {
        rectBottom = null
        rectLeft = null
        rectRight = null
        rectTop = null
    }
}