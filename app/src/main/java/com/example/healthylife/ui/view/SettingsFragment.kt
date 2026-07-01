package com.example.healthylife.ui.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import com.example.healthylife.R
import com.example.healthylife.data.DummyData
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.databinding.FragmentSettingsBinding
import com.example.healthylife.model.User
import com.example.healthylife.util.ThemePrefs

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { HealthRepository(requireContext().applicationContext) }
    private var currentUser: User = DummyData.currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = repository.getUser(1) ?: DummyData.currentUser
        bindUser(currentUser)

        binding.btnBack.setOnClickListener { (activity as? Navigator)?.goBack() }

        // Preview BMI mengikuti perubahan tinggi/berat
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) { updateBmiPreview() }
        }
        binding.etHeight.addTextChangedListener(watcher)
        binding.etWeight.addTextChangedListener(watcher)

        // Switch tema
        val dark = ThemePrefs.isDark(requireContext())
        binding.switchDark.isChecked = dark
        binding.tvThemeLabel.text = if (dark) "Mode Gelap" else "Mode Terang"
        binding.switchDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != ThemePrefs.isDark(requireContext())) {
                ThemePrefs.toggle(requireContext()) // Activity akan dibuat ulang
            }
        }

        binding.btnSave.setOnClickListener { save() }
    }

    private fun bindUser(user: User) {
        binding.etName.setText(user.name)
        binding.etAge.setText(user.age.toString())
        binding.etHeight.setText(user.height.toInt().toString())
        binding.etWeight.setText(user.weight.toInt().toString())
        binding.etTargetCalories.setText(user.targetCalories.toString())
        binding.etTargetSleep.setText(user.targetSleepHours.toString())
        binding.etTargetExercise.setText(user.targetExerciseMinutes.toString())
        updateBmiPreview()
    }

    private fun updateBmiPreview() {
        val h = binding.etHeight.text.toString().toFloatOrNull() ?: currentUser.height
        val w = binding.etWeight.text.toString().toFloatOrNull() ?: currentUser.weight
        val bmi = if (h > 0f) w / ((h / 100f) * (h / 100f)) else 0f
        binding.tvSettingsBmi.text = String.format("%.1f", bmi)

        val (label, colorRes) = when {
            bmi < 18.5 -> "Kurus" to R.color.accent_teal
            bmi < 25f  -> "Normal" to R.color.health_green
            bmi < 30f  -> "Gemuk" to R.color.card_pink
            else       -> "Obesitas" to R.color.card_pink
        }
        val color = ContextCompat.getColor(requireContext(), colorRes)
        binding.tvSettingsBmiStatus.text = label
        binding.tvSettingsBmiStatus.setTextColor(color)
        binding.tvSettingsBmiStatus.backgroundTintList =
            ColorStateList.valueOf(ColorUtils.setAlphaComponent(color, 38))
    }

    private fun save() {
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etName.error = "Nama tidak boleh kosong"
            return
        }
        val updated = User(
            id = currentUser.id,
            name = name,
            age = binding.etAge.text.toString().toIntOrNull() ?: currentUser.age,
            weight = binding.etWeight.text.toString().toFloatOrNull() ?: currentUser.weight,
            height = binding.etHeight.text.toString().toFloatOrNull() ?: currentUser.height,
            targetCalories = binding.etTargetCalories.text.toString().toIntOrNull() ?: currentUser.targetCalories,
            targetSleepHours = (binding.etTargetSleep.text.toString().toFloatOrNull()
                ?: currentUser.targetSleepHours).coerceIn(0f, 24f),
            targetExerciseMinutes = binding.etTargetExercise.text.toString().toIntOrNull()
                ?: currentUser.targetExerciseMinutes
        )
        repository.updateUser(updated)
        currentUser = updated
        binding.tvSaved.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
