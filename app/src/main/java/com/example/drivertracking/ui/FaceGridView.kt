package com.example.drivertracking.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class FaceGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = 0xFF00FF00.toInt() // Green color
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private var boundingBox: Rect? = null

    fun setFaceBoundingBox(rect: Rect?) {
        boundingBox = rect
        invalidate() // Request a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        boundingBox?.let {
            canvas.drawRect(it, paint)
        }
    }
}
