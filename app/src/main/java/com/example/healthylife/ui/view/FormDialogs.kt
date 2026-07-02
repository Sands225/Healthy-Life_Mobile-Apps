package com.example.healthylife.ui.view

import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.healthylife.R
import com.example.healthylife.databinding.DialogEditSleepBinding
import com.example.healthylife.databinding.DialogExerciseFormBinding
import com.example.healthylife.databinding.DialogFoodFormBinding
import com.example.healthylife.model.Exercise
import com.example.healthylife.model.Food
import com.example.healthylife.model.SleepRecord
import com.example.healthylife.ui.view.widget.Segmented
import com.example.healthylife.util.DateUtils

/**
 * Dialog form bersama agar input di Beranda (Tambah Cepat) sama persis
 * dengan input di menu dedicated (Makanan / Olahraga / Tidur).
 */
object FormDialogs {

    private val foodEmojis = listOf("🍚","🍛","🍜","🥗","🍗","🥩","🐟","🥚","🥣","🥞","🍕","🍔","🌮","🥙","🥤","☕","🍎","🍌","🥑","🧆")
    private val exerciseEmojis = listOf("🏃","🚶","🧘","🏋️","🚴","🏊","⛷️","🤸","🥊","🏸","⚽","🎾","🧗","🤽","🏇")
    private val foodCategories = listOf("Sarapan", "Makan Siang", "Makan Malam", "Makanan Ringan")
    private val sleepQualities = listOf("Baik", "Cukup", "Buruk")

    // ── Makanan ──────────────────────────────────────────────────────────────
    fun showFood(fragment: Fragment, initial: Food?, onSave: (Food) -> Unit) {
        val db = DialogFoodFormBinding.inflate(fragment.layoutInflater)
        var selectedEmoji = initial?.emoji ?: ""
        buildEmojiGrid(fragment, db.emojiGrid, foodEmojis, { selectedEmoji }, { selectedEmoji = it })

        initial?.let {
            db.etName.setText(it.name)
            db.etCalories.setText(it.calories.toString())
            db.etCarbs.setText(it.carbs.toInt().toString())
            db.etProtein.setText(it.protein.toInt().toString())
            db.etFat.setText(it.fat.toInt().toString())
            db.etFiber.setText(it.fiber.toInt().toString())
        }
        val catSeg = Segmented(
            listOf(db.chipSarapan, db.chipSiang, db.chipMalam, db.chipRingan),
            initial = foodCategories.indexOf(initial?.mealType).coerceAtLeast(0)
        ) { }

        AlertDialog.Builder(fragment.requireContext())
            .setTitle(if (initial != null && initial.id != 0) "Edit Makanan" else "Tambah Makanan")
            .setView(db.root)
            .setPositiveButton("Simpan") { _, _ ->
                val name = db.etName.text.toString().trim()
                if (name.isEmpty() || selectedEmoji.isEmpty()) return@setPositiveButton
                onSave(
                    Food(
                        id = initial?.id ?: 0,
                        name = name,
                        emoji = selectedEmoji,
                        calories = db.etCalories.text.toString().toIntOrNull() ?: 0,
                        carbs = db.etCarbs.text.toString().toFloatOrNull() ?: 0f,
                        protein = db.etProtein.text.toString().toFloatOrNull() ?: 0f,
                        fat = db.etFat.text.toString().toFloatOrNull() ?: 0f,
                        fiber = db.etFiber.text.toString().toFloatOrNull() ?: 0f,
                        mealType = foodCategories[catSeg.selected],
                        date = initial?.date ?: DateUtils.getTodayDateString()
                    )
                )
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ── Olahraga ─────────────────────────────────────────────────────────────
    fun showExercise(fragment: Fragment, initial: Exercise?, onSave: (Exercise) -> Unit) {
        val db = DialogExerciseFormBinding.inflate(fragment.layoutInflater)
        var selectedEmoji = initial?.emoji ?: exerciseEmojis.first()
        buildEmojiGrid(fragment, db.emojiGrid, exerciseEmojis, { selectedEmoji }, { selectedEmoji = it })

        initial?.let {
            db.etName.setText(it.name)
            db.etDuration.setText(it.durationMinutes.toString())
            db.etCalories.setText(it.caloriesBurned.toString())
        }

        AlertDialog.Builder(fragment.requireContext())
            .setTitle(if (initial != null && initial.id != 0) "Edit Aktivitas" else "Tambah Aktivitas")
            .setView(db.root)
            .setPositiveButton("Simpan") { _, _ ->
                val name = db.etName.text.toString().trim()
                if (name.isEmpty()) return@setPositiveButton
                onSave(
                    Exercise(
                        id = initial?.id ?: 0,
                        name = name,
                        emoji = selectedEmoji,
                        durationMinutes = db.etDuration.text.toString().toIntOrNull() ?: 30,
                        caloriesBurned = db.etCalories.text.toString().toIntOrNull() ?: 0,
                        date = initial?.date ?: DateUtils.getTodayDateString()
                    )
                )
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ── Tidur (jam + kualitas) ───────────────────────────────────────────────
    fun showSleep(fragment: Fragment, initial: SleepRecord?, onSave: (SleepRecord) -> Unit) {
        val db = DialogEditSleepBinding.inflate(fragment.layoutInflater)
        db.etHours.setText((initial?.durationHours ?: 8f).toString())
        val seg = Segmented(
            listOf(db.chipBaik, db.chipCukup, db.chipBuruk),
            initial = sleepQualities.indexOf(initial?.quality).coerceAtLeast(0)
        ) { }

        AlertDialog.Builder(fragment.requireContext())
            .setTitle(if (initial != null && initial.id != 0) "Edit Log Tidur" else "Log Tidur")
            .setView(db.root)
            .setPositiveButton("Simpan") { _, _ ->
                val hours = (db.etHours.text.toString().toFloatOrNull() ?: 8f).coerceIn(0f, 24f)
                onSave(
                    SleepRecord(
                        id = initial?.id ?: 0,
                        date = initial?.date ?: DateUtils.getTodayDateString(),
                        bedTime = "", wakeTime = "",
                        durationHours = hours,
                        quality = sleepQualities[seg.selected]
                    )
                )
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun buildEmojiGrid(
        fragment: Fragment,
        grid: GridLayout,
        options: List<String>,
        current: () -> String,
        onSelect: (String) -> Unit
    ) {
        val density = fragment.resources.displayMetrics.density
        val size = (44 * density).toInt()
        val margin = (4 * density).toInt()
        val views = mutableListOf<TextView>()
        fun refresh() {
            views.forEachIndexed { i, tv ->
                tv.setBackgroundResource(
                    if (options[i] == current()) R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected
                )
            }
        }
        options.forEach { emoji ->
            val tv = TextView(fragment.requireContext()).apply {
                text = emoji; textSize = 20f; gravity = Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = size; height = size; setMargins(margin, margin, margin, margin)
                }
                setOnClickListener { onSelect(emoji); refresh() }
            }
            views.add(tv); grid.addView(tv)
        }
        refresh()
    }
}
