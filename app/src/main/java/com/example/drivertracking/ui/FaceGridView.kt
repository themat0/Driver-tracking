package com.example.drivertracking.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark

class FaceGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = 0xFF00FF00.toInt() // Green color
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private var face: Face? = null

    fun setFace(face: Face?) {
        this.face = face
        invalidate() // Request a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        face?.let { face ->
            val landmarks = face.allLandmarks.map { it.position }
            for (i in landmarks.indices) {
                for (j in i + 1 until landmarks.size) {
                    canvas.drawLine(landmarks[i].x, landmarks[i].y, landmarks[j].x, landmarks[j].y, paint)
                }
            }
        }
    }
}
