import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.face.Face

class FaceGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var face: Face? = null
    private var drawMesh = true
    private val paint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    fun setFace(face: Face?) {
        this.face = face
        invalidate()
    }

    fun setDrawMesh(enabled: Boolean) {
        drawMesh = enabled
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        face?.let {
            val boundingBox: Rect = it.boundingBox
            canvas.drawRect(boundingBox, paint)
            if (drawMesh) {
                drawMesh(canvas, boundingBox)
            }
        }
    }

    private fun drawMesh(canvas: Canvas, boundingBox: Rect) {
        val stepX = boundingBox.width() / 10f
        val stepY = boundingBox.height() / 10f
        for (i in 0..10) {
            val startX = boundingBox.left + i * stepX
            canvas.drawLine(startX, boundingBox.top.toFloat(), startX, boundingBox.bottom.toFloat(), paint)
            val startY = boundingBox.top + i * stepY
            canvas.drawLine(boundingBox.left.toFloat(), startY, boundingBox.right.toFloat(), startY, paint)
        }
    }
}
