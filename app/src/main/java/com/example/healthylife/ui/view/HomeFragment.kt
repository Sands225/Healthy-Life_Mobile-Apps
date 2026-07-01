package com.example.healthylife.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.healthylife.R
import com.example.healthylife.data.DummyData
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.databinding.FragmentHomeBinding
import com.example.healthylife.databinding.ItemProgressRingBinding
import com.example.healthylife.databinding.ItemTodayRowBinding
import com.example.healthylife.model.Exercise
import com.example.healthylife.model.Food
import com.example.healthylife.model.SleepRecord
import com.example.healthylife.model.User
import com.example.healthylife.util.DateUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { HealthRepository(requireContext().applicationContext) }

    private var user: User = DummyData.currentUser
    private var exercises: List<Exercise> = DummyData.exercises
    private var foods: List<Food> = DummyData.foods
    private var sleeps: List<SleepRecord> = DummyData.sleepRecords

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnThemeToggle.setImageResource(
            if (com.example.healthylife.util.ThemePrefs.isDark(requireContext()))
                R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )
        binding.btnThemeToggle.setOnClickListener {
            com.example.healthylife.util.ThemePrefs.toggle(requireContext())
        }

        binding.qaSleep.root.setOnClickListener { showSleepDialog() }
        binding.qaFood.root.setOnClickListener { showFoodDialog() }
        binding.qaExercise.root.setOnClickListener { showExerciseDialog() }
    }

    override fun onResume() {
        super.onResume()
        loadData()
        render()
    }

    private fun loadData() {
        repository.getUser(1)?.let { user = it }
        exercises = repository.getAllExercises().ifEmpty { DummyData.exercises }
        foods = repository.getAllFoods().ifEmpty { DummyData.foods }
        sleeps = repository.getAllSleepRecords().ifEmpty { DummyData.sleepRecords }
    }

    private fun render() {
        binding.tvName.text = user.name
        binding.tvAvatar.text = user.name.take(1).uppercase()

        val todayFoods = foods.filter { DateUtils.isToday(it.date) }
        val todayExercises = exercises.filter { DateUtils.isToday(it.date) }
        val sleep = sleeps.firstOrNull { DateUtils.isToday(it.date) }
            ?: sleeps.firstOrNull() ?: DummyData.lastNightSleep

        val totalCalories = todayFoods.sumOf { it.calories }
        val minutes = todayExercises.sumOf { it.durationMinutes }
        val hours = sleep.durationHours

        val slateLight = ContextCompat.getColor(requireContext(), R.color.app_slate_light)
        val green = ContextCompat.getColor(requireContext(), R.color.health_green)
        val teal = ContextCompat.getColor(requireContext(), R.color.accent_teal)
        val sage = ContextCompat.getColor(requireContext(), R.color.accent_sage)

        bindRing(binding.ringCalories, "🍽️", "$totalCalories", "cal", "Kalori",
            totalCalories.toFloat() / user.targetCalories, green, slateLight)
        bindRing(binding.ringExercise, "💪", "$minutes", "menit", "Olahraga",
            minutes.toFloat() / user.targetExerciseMinutes, teal, slateLight)
        bindRing(binding.ringSleep, "🌙", "${hours.toInt()}", "jam", "Tidur",
            hours / user.targetSleepHours, sage, slateLight)

        // Quick-add cards
        binding.qaSleep.tvIcon.text = "🌙"
        binding.qaSleep.tvLabel.text = "Tidur"
        binding.qaSleep.tvSub.text = "${hours.toInt()} jam"
        binding.qaSleep.tvSub.setTextColor(sage)
        binding.qaFood.tvIcon.text = "🍽️"
        binding.qaFood.tvLabel.text = "Makanan"
        binding.qaFood.tvSub.text = "$totalCalories cal"
        binding.qaFood.tvSub.setTextColor(green)
        binding.qaExercise.tvIcon.text = "💪"
        binding.qaExercise.tvLabel.text = "Olahraga"
        binding.qaExercise.tvSub.text = "$minutes mnt"
        binding.qaExercise.tvSub.setTextColor(teal)

        // Aktivitas hari ini
        binding.activityContainer.removeAllViews()
        if (todayExercises.isEmpty()) {
            addEmptyRow(binding.activityContainer, "Belum ada aktivitas hari ini")
        } else {
            todayExercises.forEach { ex ->
                val row = ItemTodayRowBinding.inflate(layoutInflater, binding.activityContainer, false)
                row.tvEmoji.text = ex.emoji
                row.tvTitle.text = ex.name
                row.tvSubtitle.text = "${ex.durationMinutes} menit · ${ex.caloriesBurned} cal"
                row.tvTrailing.text = "Selesai ✓"
                binding.activityContainer.addView(row.root)
            }
        }

        // Makanan hari ini
        binding.tvCaloriesSummary.text = "$totalCalories / ${user.targetCalories} cal"
        binding.progressCalories.progress =
            ((totalCalories.toFloat() / user.targetCalories) * 100).coerceIn(0f, 100f).toInt()
        binding.tvCarbs.text = "${todayFoods.sumOf { it.carbs.toDouble() }.toInt()}g"
        binding.tvProtein.text = "${todayFoods.sumOf { it.protein.toDouble() }.toInt()}g"
        binding.tvFat.text = "${todayFoods.sumOf { it.fat.toDouble() }.toInt()}g"
        binding.tvFiber.text = "${todayFoods.sumOf { it.fiber.toDouble() }.toInt()}g"

        binding.foodContainer.removeAllViews()
        if (todayFoods.isEmpty()) {
            addEmptyRow(binding.foodContainer, "Belum ada makanan hari ini")
        } else {
            todayFoods.take(4).forEach { food ->
                val row = ItemTodayRowBinding.inflate(layoutInflater, binding.foodContainer, false)
                row.tvEmoji.text = food.emoji
                row.tvTitle.text = food.name
                row.tvSubtitle.text = "${food.calories} cal · ${food.mealType}"
                row.tvTrailing.text = ""
                binding.foodContainer.addView(row.root)
            }
        }
    }

    private fun bindRing(
        ring: ItemProgressRingBinding,
        emoji: String, value: String, unit: String, label: String,
        fraction: Float, accent: Int, track: Int
    ) {
        ring.ring.setProgress(fraction, accent, track)
        ring.tvEmoji.text = emoji
        ring.tvValue.text = value
        ring.tvUnit.text = unit
        ring.tvUnit.setTextColor(accent)
        ring.tvLabel.text = label
    }

    private fun addEmptyRow(container: ViewGroup, text: String) {
        val tv = android.widget.TextView(requireContext()).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(requireContext(), R.color.app_text_muted))
            textSize = 13f
            setPadding(0, 12, 0, 12)
        }
        container.addView(tv)
    }

    // ── Dialog Tambah Cepat ─────────────────────────────────────────────────────
    private fun showSleepDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Jam tidur (0-24)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText("8")
        }
        AlertDialog.Builder(requireContext())
            .setTitle("🌙 Log Tidur")
            .setView(pad(input))
            .setPositiveButton("Simpan") { _, _ ->
                val h = (input.text.toString().toFloatOrNull() ?: 8f).coerceIn(0f, 24f)
                repository.insertSleepRecord(SleepRecord(0, DateUtils.getTodayDateString(), "", "", h, "Cukup"))
                loadData(); render()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showFoodDialog() {
        val options = DummyData.foods.distinctBy { it.name }
        val names = options.map { "${it.emoji} ${it.name} (${it.calories} cal)" }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("🍽️ Log Makanan")
            .setItems(names) { _, which ->
                val f = options[which]
                repository.insertFood(f.copy(id = 0, date = DateUtils.getTodayDateString()))
                loadData(); render()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showExerciseDialog() {
        val input = EditText(requireContext()).apply {
            hint = "Durasi (menit)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText("30")
        }
        AlertDialog.Builder(requireContext())
            .setTitle("💪 Log Olahraga")
            .setView(pad(input))
            .setPositiveButton("Simpan") { _, _ ->
                val m = input.text.toString().toIntOrNull() ?: 30
                repository.insertExercise(Exercise(0, "Lari", "🏃", m, m * 9, DateUtils.getTodayDateString()))
                loadData(); render()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun pad(v: View): View {
        val p = (24 * resources.displayMetrics.density).toInt()
        return android.widget.FrameLayout(requireContext()).apply {
            setPadding(p, p / 2, p, 0)
            addView(v)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
