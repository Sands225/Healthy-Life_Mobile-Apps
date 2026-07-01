package com.example.healthylife.ui.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Ring progress melingkar berbasis Canvas (untuk kartu progres Beranda).
 * Teks (emoji/nilai/unit) ditaruh sebagai TextView terpisah di atasnya.
 */
class RingProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var progress: Float = 0f
    private var accent: Int = Color.parseColor("#2D9E6B")
    private var trackColor: Int = Color.parseColor("#1F3828")

    private val density = resources.displayMetrics.density
    private val stroke = 8f * density

    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = stroke
    }
    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = stroke
    }

    fun setProgress(fraction: Float, accentColor: Int, track: Int) {
        this.progress = fraction.coerceIn(0f, 1f)
        this.accent = accentColor
        this.trackColor = track
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = RectF(
            stroke / 2f, stroke / 2f,
            width - stroke / 2f, height - stroke / 2f
        )
        trackPaint.color = trackColor
        canvas.drawArc(rect, -90f, 360f, false, trackPaint)

        progressPaint.color = accent
        canvas.drawArc(rect, -90f, 360f * progress, false, progressPaint)
    }
}
