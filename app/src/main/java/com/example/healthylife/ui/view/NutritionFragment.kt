package com.example.healthylife.ui.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthylife.R
import com.example.healthylife.data.DummyData
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.databinding.DialogFoodFormBinding
import com.example.healthylife.databinding.FragmentNutritionBinding
import com.example.healthylife.model.Food
import com.example.healthylife.model.User
import com.example.healthylife.ui.view.adapter.FoodAdapter
import com.example.healthylife.ui.view.widget.AnalyticsBinder
import com.example.healthylife.ui.view.widget.Segmented
import com.example.healthylife.util.DateUtils
import com.example.healthylife.util.ThemePrefs
import com.example.healthylife.util.TimeFilter

class NutritionFragment : Fragment() {

    private var _binding: FragmentNutritionBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { HealthRepository(requireContext().applicationContext) }
    private var user: User = DummyData.currentUser
    private var foods: List<Food> = DummyData.foods

    private lateinit var adapter: FoodAdapter
    private lateinit var analytics: AnalyticsBinder
    private lateinit var filter: Segmented
    private lateinit var category: Segmented
    private var search = ""

    private val categoryValues = listOf("Semua", "Sarapan", "Makan Siang", "Makan Malam", "Makanan Ringan")
    private val formCategories = listOf("Sarapan", "Makan Siang", "Makan Malam", "Makanan Ringan")
    private val emojiOptions = listOf("🍚","🍛","🍜","🥗","🍗","🥩","🐟","🥚","🥣","🥞","🍕","🍔","🌮","🥙","🥤","☕","🍎","🍌","🥑","🧆")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNutritionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnThemeToggle.setImageResource(
            if (ThemePrefs.isDark(requireContext())) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )
        binding.btnThemeToggle.setOnClickListener { ThemePrefs.toggle(requireContext()) }

        adapter = FoodAdapter(emptyList(), onEdit = { showForm(it) }, onDelete = ::confirmDelete)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        analytics = AnalyticsBinder(
            binding.analytics, unit = "cal",
            accentColor = color(R.color.health_green),
            trackColor = color(R.color.app_slate_light),
            labelColor = color(R.color.app_text_muted),
            stateKey = "nutrition"
        )

        filter = Segmented(
            listOf(binding.filter.chipToday, binding.filter.chipWeek, binding.filter.chipAll),
            initial = 0
        ) { renderList() }

        category = Segmented(
            listOf(binding.catAll, binding.catSarapan, binding.catSiang, binding.catMalam, binding.catRingan),
            initial = 0
        ) { renderList() }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) { search = s?.toString() ?: ""; renderList() }
        })

        binding.btnAdd.setOnClickListener { showForm(null) }
    }

    override fun onResume() {
        super.onResume()
        loadData(); renderAll()
    }

    private fun loadData() {
        repository.getUser(1)?.let { user = it }
        foods = repository.getAllFoods().ifEmpty { DummyData.foods }
    }

    private fun renderAll() {
        val today = foods.filter { DateUtils.isToday(it.date) }
        val totalCal = today.sumOf { it.calories }
        binding.tvTotalCal.text = "$totalCal"
        binding.tvPercent.text = "${((totalCal.toFloat() / user.targetCalories) * 100).toInt()}%"
        binding.progressCal.progress = ((totalCal.toFloat() / user.targetCalories) * 100).coerceIn(0f, 100f).toInt()
        binding.tvCarbs.text = "${today.sumOf { it.carbs.toDouble() }.toInt()}g"
        binding.tvProtein.text = "${today.sumOf { it.protein.toDouble() }.toInt()}g"
        binding.tvFat.text = "${today.sumOf { it.fat.toDouble() }.toInt()}g"
        binding.tvFiber.text = "${today.sumOf { it.fiber.toDouble() }.toInt()}g"

        analytics.setData(foods.map { it.date to it.calories.toFloat() })
        renderList()
    }

    private fun renderList() {
        val mode = TimeFilter.values()[filter.selected]
        val cat = categoryValues[category.selected]
        val filtered = foods.filter { f ->
            mode.matches(f.date) &&
                (cat == "Semua" || f.mealType == cat) &&
                (search.isBlank() || f.name.contains(search, ignoreCase = true))
        }
        adapter.submit(filtered)
        binding.tvCount.text = "${filtered.size} item"
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.recycler.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showForm(initial: Food?) {
        val db = DialogFoodFormBinding.inflate(layoutInflater)
        var selectedEmoji = initial?.emoji ?: ""

        // Grid emoji
        val size = (44 * resources.displayMetrics.density).toInt()
        val margin = (4 * resources.displayMetrics.density).toInt()
        val emojiViews = mutableListOf<TextView>()
        fun refreshEmoji() {
            emojiViews.forEachIndexed { i, tv ->
                tv.setBackgroundResource(
                    if (emojiOptions[i] == selectedEmoji) R.drawable.bg_chip_selected else R.drawable.bg_chip_unselected
                )
            }
        }
        emojiOptions.forEach { emoji ->
            val tv = TextView(requireContext()).apply {
                text = emoji
                textSize = 20f
                gravity = Gravity.CENTER
                val lp = GridLayout.LayoutParams().apply {
                    width = size; height = size; setMargins(margin, margin, margin, margin)
                }
                layoutParams = lp
                setOnClickListener { selectedEmoji = emoji; refreshEmoji() }
            }
            emojiViews.add(tv)
            db.emojiGrid.addView(tv)
        }
        refreshEmoji()

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
            initial = formCategories.indexOf(initial?.mealType).coerceAtLeast(0)
        ) { }

        AlertDialog.Builder(requireContext())
            .setTitle(if (initial != null) "Edit Makanan" else "Tambah Makanan")
            .setView(db.root)
            .setPositiveButton("Simpan") { _, _ ->
                val name = db.etName.text.toString().trim()
                if (name.isEmpty() || selectedEmoji.isEmpty()) return@setPositiveButton
                val food = Food(
                    id = initial?.id ?: 0,
                    name = name,
                    emoji = selectedEmoji,
                    calories = db.etCalories.text.toString().toIntOrNull() ?: 0,
                    carbs = db.etCarbs.text.toString().toFloatOrNull() ?: 0f,
                    protein = db.etProtein.text.toString().toFloatOrNull() ?: 0f,
                    fat = db.etFat.text.toString().toFloatOrNull() ?: 0f,
                    fiber = db.etFiber.text.toString().toFloatOrNull() ?: 0f,
                    mealType = formCategories[catSeg.selected],
                    date = initial?.date ?: DateUtils.getTodayDateString()
                )
                if (food.id == 0) repository.insertFood(food) else repository.updateFood(food)
                loadData(); renderAll()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmDelete(food: Food) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Makanan")
            .setMessage("Hapus \"${food.name}\"?")
            .setPositiveButton("Hapus") { _, _ -> repository.deleteFood(food.id); loadData(); renderAll() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun color(res: Int) = ContextCompat.getColor(requireContext(), res)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
