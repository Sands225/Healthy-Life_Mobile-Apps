package com.example.healthylife.ui.view.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.max
import kotlin.math.min

/**
 * View kotak untuk mengatur (geser + cubit-zoom) foto sebelum disimpan
 * sebagai avatar lingkaran. Tanpa library eksternal.
 */
class CropImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var bitmap: Bitmap? = null
    private val matrix = Matrix()
    private var minScale = 1f

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }
    private val dimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#99000000")
    }
    private val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 2f * resources.displayMetrics.density
    }

    private var lastX = 0f
    private var lastY = 0f
    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val current = currentScale()
                var factor = detector.scaleFactor
                if (current * factor < minScale) factor = minScale / current
                if (current * factor > minScale * 5f) factor = minScale * 5f / current
                matrix.postScale(factor, factor, detector.focusX, detector.focusY)
                clamp()
                invalidate()
                return true
            }
        })

    fun setBitmap(bmp: Bitmap) {
        bitmap = bmp
        if (width > 0 && height > 0) resetMatrix()
        invalidate()
    }

    /** Putar foto 90° searah jarum jam. */
    fun rotate90() {
        val bmp = bitmap ?: return
        val m = Matrix().apply { postRotate(90f) }
        bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
        if (width > 0 && height > 0) resetMatrix()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (bitmap != null) resetMatrix()
    }

    private fun resetMatrix() {
        val bmp = bitmap ?: return
        val scale = max(width.toFloat() / bmp.width, height.toFloat() / bmp.height)
        minScale = scale
        matrix.reset()
        matrix.postScale(scale, scale)
        matrix.postTranslate((width - bmp.width * scale) / 2f, (height - bmp.height * scale) / 2f)
    }

    private fun currentScale(): Float {
        val v = FloatArray(9); matrix.getValues(v); return v[Matrix.MSCALE_X]
    }

    private fun clamp() {
        val bmp = bitmap ?: return
        val v = FloatArray(9); matrix.getValues(v)
        val scale = v[Matrix.MSCALE_X]
        val transX = v[Matrix.MTRANS_X]
        val transY = v[Matrix.MTRANS_Y]
        val bw = bmp.width * scale
        val bh = bmp.height * scale
        val newX = transX.coerceIn(min(width - bw, 0f), max(width - bw, 0f))
        val newY = transY.coerceIn(min(height - bh, 0f), max(height - bh, 0f))
        matrix.postTranslate(newX - transX, newY - transY)
    }

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { lastX = event.x; lastY = event.y }
            MotionEvent.ACTION_MOVE -> if (!scaleDetector.isInProgress) {
                matrix.postTranslate(event.x - lastX, event.y - lastY)
                lastX = event.x; lastY = event.y
                clamp(); invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bmp = bitmap ?: return
        canvas.drawBitmap(bmp, matrix, bitmapPaint)

        // Overlay gelap + lubang lingkaran
        val layer = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)
        val r = min(width, height) / 2f
        canvas.drawCircle(width / 2f, height / 2f, r, clearPaint)
        canvas.restoreToCount(layer)
        canvas.drawCircle(width / 2f, height / 2f, r, ringPaint)
    }

    /** Render area kotak saat ini menjadi bitmap persegi berukuran [outSize]. */
    fun getResult(outSize: Int): Bitmap? {
        val bmp = bitmap ?: return null
        val square = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(square).drawBitmap(bmp, matrix, bitmapPaint)
        return Bitmap.createScaledBitmap(square, outSize, outSize, true)
    }
}
