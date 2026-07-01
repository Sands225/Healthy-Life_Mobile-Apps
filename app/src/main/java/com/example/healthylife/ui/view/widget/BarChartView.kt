package com.example.healthylife.ui.view.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Bar chart sederhana berbasis Canvas (tanpa library).
 * Dipakai untuk analitik Olahraga / Makanan / Tidur.
 */
class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var values: List<Float> = emptyList()
    private var labels: List<String> = emptyList()
    private var accent: Int = Color.parseColor("#2D9E6B")
    private var trackColor: Int = Color.parseColor("#1F3828")
    private var labelColor: Int = Color.parseColor("#7DAA8E")
    private var valueFormatter: (Float) -> String = { it.toInt().toString() }

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
    }

    private val density = resources.displayMetrics.density
    private fun dp(v: Float) = v * density

    fun setData(
        values: List<Float>,
        labels: List<String>,
        accentColor: Int,
        trackColor: Int,
        labelColor: Int,
        formatter: (Float) -> String = { it.toInt().toString() }
    ) {
        this.values = values
        this.labels = labels
        this.accent = accentColor
        this.trackColor = trackColor
        this.labelColor = labelColor
        this.valueFormatter = formatter
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (values.isEmpty()) return

        val n = values.size
        val topPad = dp(16f)      // ruang untuk teks nilai
        val bottomPad = dp(16f)   // ruang untuk label
        val chartTop = topPad
        val chartBottom = height - bottomPad
        val chartHeight = chartBottom - chartTop

        val maxVal = (values.maxOrNull() ?: 0f).coerceAtLeast(1f)
        val slot = width.toFloat() / n
        val barWidth = (slot * 0.5f).coerceAtMost(dp(22f))
        val corner = dp(4f)

        textPaint.textSize = dp(9f)

        for (i in 0 until n) {
            val v = values[i]
            val fraction = (v / maxVal).coerceIn(0f, 1f)
            val cx = slot * i + slot / 2f
            val barLeft = cx - barWidth / 2f
            val barRight = cx + barWidth / 2f
            val barHeight = (fraction * (chartHeight - dp(14f))).coerceAtLeast(dp(3f))
            val barTop = chartBottom - barHeight

            // Track tipis
            barPaint.color = trackColor
            canvas.drawRoundRect(
                RectF(barLeft, chartTop + dp(12f), barRight, chartBottom),
                corner, corner, barPaint
            )

            // Bar nilai
            if (v > 0f) {
                barPaint.color = accent
                canvas.drawRoundRect(
                    RectF(barLeft, barTop, barRight, chartBottom),
                    corner, corner, barPaint
                )
                // Nilai di atas bar
                textPaint.color = accent
                textPaint.isFakeBoldText = true
                canvas.drawText(valueFormatter(v), cx, barTop - dp(3f), textPaint)
            }

            // Label di bawah
            textPaint.color = labelColor
            textPaint.isFakeBoldText = false
            canvas.drawText(labels.getOrElse(i) { "" }, cx, height - dp(3f), textPaint)
        }
    }
}
