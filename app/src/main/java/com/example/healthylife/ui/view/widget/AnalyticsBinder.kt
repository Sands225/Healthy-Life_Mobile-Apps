package com.example.healthylife.ui.view.widget

import android.view.View
import com.example.healthylife.databinding.ViewAnalyticsBinding
import com.example.healthylife.util.Analytics

/**
 * Menghubungkan include view_analytics.xml dengan data & mode
 * (Hari Ini ringkasan / Minggu Ini bar / Semua bar per-minggu).
 */
class AnalyticsBinder(
    private val binding: ViewAnalyticsBinding,
    private val unit: String,
    private val accentColor: Int,
    private val trackColor: Int,
    private val labelColor: Int,
    private val stateKey: String,
    private val formatter: (Float) -> String = { it.toInt().toString() }
) {
    private var data: List<Pair<String, Float>> = emptyList()

    private val segmented = Segmented(
        listOf(binding.chipToday, binding.chipWeek, binding.chipAll),
        initial = savedModes[stateKey] ?: 1
    ) {
        savedModes[stateKey] = it
        render()
    }

    companion object {
        // Menyimpan mode terpilih per layar agar tetap saat pindah halaman.
        private val savedModes = mutableMapOf<String, Int>()
    }

    fun setData(newData: List<Pair<String, Float>>) {
        data = newData
        render()
    }

    private fun render() {
        when (segmented.selected) {
            0 -> {
                binding.analyticsChart.visibility = View.GONE
                binding.analyticsSummary.visibility = View.VISIBLE
                binding.tvSummaryValue.text = "${formatter(Analytics.todayTotal(data))} $unit"
                binding.tvSummaryValue.setTextColor(accentColor)
                binding.tvSummaryCount.text = "${Analytics.todayCount(data)} catatan"
            }
            1 -> {
                showChart()
                val (values, labels) = Analytics.weekly(data)
                binding.analyticsChart.setData(values, labels, accentColor, trackColor, labelColor, formatter)
            }
            else -> {
                showChart()
                val (values, labels) = Analytics.allWeeks(data)
                binding.analyticsChart.setData(values, labels, accentColor, trackColor, labelColor, formatter)
            }
        }
    }

    private fun showChart() {
        binding.analyticsChart.visibility = View.VISIBLE
        binding.analyticsSummary.visibility = View.GONE
    }
}
