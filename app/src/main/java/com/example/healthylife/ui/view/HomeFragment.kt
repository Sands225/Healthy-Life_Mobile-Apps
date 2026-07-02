package com.example.healthylife.ui.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
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

        // Foto profil (jika ada)
        binding.ivAvatar.clipToOutline = true
        binding.ivAvatar.outlineProvider = object : android.view.ViewOutlineProvider() {
            override fun getOutline(v: View, outline: android.graphics.Outline) {
                outline.setOval(0, 0, v.width, v.height)
            }
        }
        val hasPhoto = com.example.healthylife.util.AvatarStore.loadInto(binding.ivAvatar)
        binding.ivAvatar.visibility = if (hasPhoto) View.VISIBLE else View.GONE
        binding.tvAvatar.visibility = if (hasPhoto) View.GONE else View.VISIBLE

        // BMI
        val bmi = if (user.height > 0f) user.weight / ((user.height / 100f) * (user.height / 100f)) else 0f
        binding.tvHomeBmi.text = String.format("%.1f", bmi)
        val (bmiLabel, bmiColorRes) = when {
            bmi < 18.5 -> "Kurus" to R.color.accent_teal
            bmi < 25f  -> "Normal ✓" to R.color.health_green
            bmi < 30f  -> "Gemuk" to R.color.card_pink
            else       -> "Obesitas" to R.color.card_pink
        }
        val bmiColor = color(bmiColorRes)
        binding.tvHomeBmiStatus.text = bmiLabel
        binding.tvHomeBmiStatus.setTextColor(bmiColor)
        binding.tvHomeBmiStatus.backgroundTintList = ColorStateList.valueOf(ColorUtils.setAlphaComponent(bmiColor, 38))
        binding.progressHomeBmi.progress = (((bmi - 15f) / 25f) * 100).coerceIn(0f, 100f).toInt()

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
                row.tvTrailing.text = "✓"
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

    private fun color(res: Int) = ContextCompat.getColor(requireContext(), res)

    private fun addEmptyRow(container: ViewGroup, text: String) {
        val tv = android.widget.TextView(requireContext()).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(requireContext(), R.color.app_text_muted))
            textSize = 13f
            setPadding(0, 12, 0, 12)
        }
        container.addView(tv)
    }

    // ── Tambah Cepat: pakai form yang sama dengan menu dedicated ─────────────────
    private fun showSleepDialog() {
        FormDialogs.showSleep(this, null) { rec ->
            repository.insertSleepRecord(rec.copy(date = DateUtils.getTodayDateString()))
            loadData(); render()
        }
    }

    private fun showFoodDialog() {
        FormDialogs.showFood(this, null) { food ->
            repository.insertFood(food.copy(date = DateUtils.getTodayDateString()))
            loadData(); render()
        }
    }

    private fun showExerciseDialog() {
        FormDialogs.showExercise(this, null) { ex ->
            repository.insertExercise(ex.copy(date = DateUtils.getTodayDateString()))
            loadData(); render()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
