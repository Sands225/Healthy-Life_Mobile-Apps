package com.example.healthylife.ui.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.healthylife.R
import com.example.healthylife.data.DummyData
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.databinding.DialogAddTemplateBinding
import com.example.healthylife.databinding.DialogExerciseFormBinding
import com.example.healthylife.databinding.DialogQuickAddBinding
import com.example.healthylife.databinding.FragmentExerciseBinding
import com.example.healthylife.databinding.ItemExerciseBinding
import com.example.healthylife.model.Exercise
import com.example.healthylife.ui.view.widget.AnalyticsBinder
import com.example.healthylife.ui.view.widget.Segmented
import com.example.healthylife.util.DateUtils
import com.example.healthylife.util.ThemePrefs
import com.example.healthylife.util.TimeFilter

class ExerciseFragment : Fragment() {

    private data class Template(val emoji: String, val name: String, val calPerMin: Int)

    private var _binding: FragmentExerciseBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { HealthRepository(requireContext().applicationContext) }
    private var exercises: List<Exercise> = DummyData.exercises

    private lateinit var analytics: AnalyticsBinder
    private lateinit var filter: Segmented

    private val emojiOptions = listOf("🏃","🚶","🧘","🏋️","🚴","🏊","⛷️","🤸","🥊","🏸","⚽","🎾","🧗","🤽","🏇")
    private val templates = mutableListOf(
        Template("🏃", "Lari", 10),
        Template("🚶", "Jalan", 4),
        Template("🧘", "Yoga", 4),
        Template("🏋️", "Gym", 9),
        Template("🚴", "Sepeda", 8),
        Template("🏊", "Renang", 11)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnThemeToggle.setImageResource(
            if (ThemePrefs.isDark(requireContext())) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )
        binding.btnThemeToggle.setOnClickListener { ThemePrefs.toggle(requireContext()) }

        analytics = AnalyticsBinder(
            binding.analytics, unit = "cal",
            accentColor = color(R.color.accent_teal),
            trackColor = color(R.color.app_slate_light),
            labelColor = color(R.color.app_text_muted),
            stateKey = "exercise"
        )

        filter = Segmented(
            listOf(binding.filter.chipToday, binding.filter.chipWeek, binding.filter.chipAll),
            initial = 0
        ) { renderList() }

        binding.fabAdd.setOnClickListener { showAddChooser() }
    }

    override fun onResume() {
        super.onResume()
        loadData(); renderAll()
    }

    private fun loadData() {
        exercises = repository.getAllExercises()
    }

    private fun renderAll() {
        val today = exercises.filter { DateUtils.isToday(it.date) }
        binding.tvSumMinutes.text = "${today.sumOf { it.durationMinutes }}"
        binding.tvSumCalories.text = "${today.sumOf { it.caloriesBurned }}"
        binding.tvSumSessions.text = "${today.size}"
        analytics.setData(exercises.map { it.date to it.caloriesBurned.toFloat() })
        renderList()
    }

    private fun renderList() {
        val mode = TimeFilter.values()[filter.selected]
        val filtered = exercises.filter { mode.matches(it.date) }
        binding.tvCount.text = "${filtered.size} sesi"
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE

        binding.historyContainer.removeAllViews()
        filtered.forEach { ex ->
            val row = ItemExerciseBinding.inflate(layoutInflater, binding.historyContainer, false)
            row.tvEmoji.text = ex.emoji
            row.tvName.text = ex.name
            row.tvDetail.text = "${ex.durationMinutes} menit · ${ex.caloriesBurned} cal"
            row.tvDate.text = DateUtils.toRelativeString(ex.date)
            row.btnMore.setOnClickListener { anchor ->
                PopupMenu(anchor.context, anchor).apply {
                    menu.add("Edit")
                    menu.add("Hapus")
                    setOnMenuItemClickListener { item ->
                        when (item.title) {
                            "Edit" -> showForm(ex)
                            "Hapus" -> confirmDelete(ex)
                        }
                        true
                    }
                    show()
                }
            }
            binding.historyContainer.addView(row.root)
        }
    }

    // ── Chooser ──────────────────────────────────────────────────────────────
    private fun showAddChooser() {
        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Aktivitas")
            .setItems(arrayOf("⚡ Tambah Cepat", "✏️ Manual")) { _, which ->
                if (which == 0) showQuickAdd() else showForm(null)
            }
            .show()
    }

    // ── Manual / Edit ────────────────────────────────────────────────────────
    private fun showForm(initial: Exercise?) {
        FormDialogs.showExercise(this, initial) { ex ->
            if (ex.id == 0) repository.insertExercise(ex) else repository.updateExercise(ex)
            loadData(); renderAll()
        }
    }

    private fun confirmDelete(ex: Exercise) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Aktivitas")
            .setMessage("Hapus catatan \"${ex.name}\"?")
            .setPositiveButton("Hapus") { _, _ -> repository.deleteExercise(ex.id); loadData(); renderAll() }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ── Quick Add ────────────────────────────────────────────────────────────
    private fun showQuickAdd() {
        val db = DialogQuickAddBinding.inflate(layoutInflater)
        var selectedTemplate: Template? = null
        var duration = 30

        fun updateEstimate() {
            db.tvDuration.text = "$duration menit"
            val cals = (selectedTemplate?.calPerMin ?: 0) * duration
            db.tvEstimate.text = "Estimasi: $cals cal"
        }

        fun refreshTemplates() {
            db.templateGrid.removeAllViews()
            templates.forEach { t ->
                val cell = buildTemplateCell(t.emoji, t.name, selectedTemplate == t)
                cell.setOnClickListener { selectedTemplate = t; refreshTemplatesKeepSelection(db, selectedTemplate); updateEstimate() }
                cell.setOnLongClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Hapus Shortcut")
                        .setMessage("Hapus \"${t.name}\"?")
                        .setPositiveButton("Hapus") { _, _ ->
                            if (selectedTemplate == t) selectedTemplate = null
                            templates.remove(t); refreshTemplates(); updateEstimate()
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                    true
                }
                db.templateGrid.addView(cell)
            }
            // Tile "+ Buat"
            val addCell = buildTemplateCell("＋", "Buat", false)
            addCell.setOnClickListener { showAddTemplate { refreshTemplates() } }
            db.templateGrid.addView(addCell)
        }

        // helper untuk memperbarui highlight tanpa membangun ulang seluruh grid
        // (didefinisikan sebagai fungsi terpisah di bawah karena butuh binding)
        refreshTemplates()
        updateEstimate()

        db.seekDuration.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                duration = progress + 5; updateEstimate()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Tambah Cepat")
            .setView(db.root)
            .setNegativeButton("Tutup", null)
            .create()

        db.btnLog.setOnClickListener {
            val t = selectedTemplate
            if (t == null) {
                android.widget.Toast.makeText(requireContext(), "Pilih aktivitas dulu", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            repository.insertExercise(
                Exercise(0, t.name, t.emoji, duration, duration * t.calPerMin, DateUtils.getTodayDateString())
            )
            loadData(); renderAll()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun refreshTemplatesKeepSelection(db: DialogQuickAddBinding, selected: Template?) {
        // Re-highlight cells: cari cell yang cocok berdasarkan indeks template
        for (i in 0 until templates.size) {
            val cell = db.templateGrid.getChildAt(i) as? LinearLayout ?: continue
            styleTemplateCell(cell, templates[i] == selected)
        }
    }

    private fun showAddTemplate(onCreated: () -> Unit) {
        val db = DialogAddTemplateBinding.inflate(layoutInflater)
        var emoji = emojiOptions.first()
        buildEmojiGrid(db.emojiGrid, { emoji }, { emoji = it })
        AlertDialog.Builder(requireContext())
            .setTitle("Buat Shortcut")
            .setView(db.root)
            .setPositiveButton("Simpan") { _, _ ->
                val name = db.etName.text.toString().trim()
                if (name.isNotEmpty() && templates.none { it.name.equals(name, true) }) {
                    templates.add(Template(emoji, name, db.etCalPerMin.text.toString().toIntOrNull() ?: 8))
                    onCreated()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // ── Emoji grid & template cell builders ──────────────────────────────────
    private fun buildEmojiGrid(grid: GridLayout, current: () -> String, onSelect: (String) -> Unit) {
        val size = (44 * resources.displayMetrics.density).toInt()
        val margin = (4 * resources.displayMetrics.density).toInt()
        val views = mutableListOf<TextView>()
        fun refresh() {
            views.forEachIndexed { i, tv ->
                tv.setBackgroundResource(
                    if (emojiOptions[i] == current()) R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected
                )
            }
        }
        emojiOptions.forEach { emoji ->
            val tv = TextView(requireContext()).apply {
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

    private fun buildTemplateCell(emoji: String, name: String, selected: Boolean): LinearLayout {
        val pad = (12 * resources.displayMetrics.density).toInt()
        val margin = (5 * resources.displayMetrics.density).toInt()
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(pad, pad, pad, pad)
            layoutParams = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
            ).apply { width = 0; setMargins(margin, margin, margin, margin) }
            addView(TextView(context).apply { text = emoji; textSize = 22f; gravity = Gravity.CENTER })
            addView(TextView(context).apply {
                text = name; textSize = 11f; gravity = Gravity.CENTER
                setTextColor(ContextCompat.getColor(context, R.color.app_text_primary))
            })
            styleTemplateCell(this, selected)
        }
    }

    private fun styleTemplateCell(cell: LinearLayout, selected: Boolean) {
        cell.setBackgroundResource(
            if (selected) R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected
        )
    }

    private fun color(res: Int) = ContextCompat.getColor(requireContext(), res)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
