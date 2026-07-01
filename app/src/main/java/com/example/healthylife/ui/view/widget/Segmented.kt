package com.example.healthylife.ui.view.widget

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.healthylife.R

/**
 * Kontrol segmented sederhana untuk sekumpulan chip TextView
 * (dipakai untuk filter waktu & pemilih mode analitik).
 */
class Segmented(
    private val chips: List<TextView>,
    initial: Int,
    private val onSelect: (Int) -> Unit
) {
    var selected: Int = initial
        private set

    init {
        chips.forEachIndexed { index, chip ->
            chip.setOnClickListener {
                selected = index
                render()
                onSelect(index)
            }
        }
        render()
    }

    private fun render() {
        chips.forEachIndexed { index, chip ->
            val ctx = chip.context
            if (index == selected) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(ContextCompat.getColor(ctx, R.color.dark_deep_navy))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(ContextCompat.getColor(ctx, R.color.app_text_secondary))
            }
        }
    }
}
