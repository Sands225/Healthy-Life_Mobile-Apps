package com.example.healthylife

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.healthylife.databinding.ActivityMainBinding
import com.example.healthylife.databinding.ItemBottomTabBinding
import com.example.healthylife.ui.view.ExerciseFragment
import com.example.healthylife.ui.view.HomeFragment
import com.example.healthylife.ui.view.Navigator
import com.example.healthylife.ui.view.NutritionFragment
import com.example.healthylife.ui.view.ProfileFragment
import com.example.healthylife.ui.view.SettingsFragment
import com.example.healthylife.ui.view.SleepFragment
import com.example.healthylife.util.ThemePrefs

class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    private data class TabDef(
        val tab: ItemBottomTabBinding,
        val iconRes: Int,
        val label: String,
        val newFragment: () -> Fragment
    )

    private lateinit var tabs: List<TabDef>
    private var selectedIndex = 0
    private var inSettings = false

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemePrefs.apply(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hindari konten menimpa status bar / navigation bar (edge-to-edge Android 15)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.container.updatePadding(top = bars.top)
            binding.bottomBar.updatePadding(bottom = bars.bottom)
            insets
        }

        tabs = listOf(
            TabDef(binding.tabHome, R.drawable.ic_home, "Beranda") { HomeFragment() },
            TabDef(binding.tabExercise, R.drawable.ic_exercise, "Olahraga") { ExerciseFragment() },
            TabDef(binding.tabNutrition, R.drawable.ic_food, "Makanan") { NutritionFragment() },
            TabDef(binding.tabSleep, R.drawable.ic_sleep, "Tidur") { SleepFragment() },
            TabDef(binding.tabProfile, R.drawable.ic_profile, "Profil") { ProfileFragment() }
        )

        tabs.forEachIndexed { index, def ->
            def.tab.ivIcon.setImageResource(def.iconRes)
            def.tab.tvLabel.text = def.label
            def.tab.root.setOnClickListener { selectTab(index) }
        }

        if (savedInstanceState == null) {
            selectTab(0)
        } else {
            selectedIndex = savedInstanceState.getInt(KEY_TAB, 0)
            updateTabTint()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (inSettings) {
                    goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun selectTab(index: Int) {
        selectedIndex = index
        inSettings = false
        binding.bottomBar.visibility = View.VISIBLE
        showFragment(tabs[index].newFragment())
        updateTabTint()
    }

    private fun updateTabTint() {
        val selected = ContextCompat.getColor(this, R.color.health_green)
        val muted = ContextCompat.getColor(this, R.color.app_text_muted)
        tabs.forEachIndexed { index, def ->
            val color = if (index == selectedIndex && !inSettings) selected else muted
            def.tab.ivIcon.setColorFilter(color)
            def.tab.tvLabel.setTextColor(color)
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    // ── Navigator ──────────────────────────────────────────────────────────────
    override fun openSettings() {
        inSettings = true
        binding.bottomBar.visibility = View.GONE
        showFragment(SettingsFragment())
    }

    override fun goBack() {
        selectTab(selectedIndex)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_TAB, selectedIndex)
    }

    companion object {
        private const val KEY_TAB = "selected_tab"
    }
}
