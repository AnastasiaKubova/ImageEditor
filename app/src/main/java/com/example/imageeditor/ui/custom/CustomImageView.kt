package com.example.imageeditor.ui.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View

class CustomImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var mBitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mBitmap != null) {
            canvas?.drawBitmap(mBitmap!!, width / 2f - mBitmap!!.width / 2f, height / 2f - mBitmap!!.height / 2f, null)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resizeBitmap()
    }

    fun setImageBitmap(bitmap: Bitmap) {
        mBitmap = bitmap
        if (width != 0) {
            resizeBitmap()
        }
        invalidate()
    }

    private fun resizeBitmap() {
        val bitmap = mBitmap ?: return
        val vRatio = width / height
        val bRation = bitmap.width / bitmap.height
        mBitmap = if (vRatio < bRation) {
            val h = width.toFloat() * bitmap.height / bitmap.width
            Bitmap.createScaledBitmap(bitmap, width, h.toInt(),false)
        } else {
            val w = height.toFloat()  * bitmap.width / bitmap.height
            Bitmap.createScaledBitmap(bitmap, w.toInt(), height,false)
        }
    }
}