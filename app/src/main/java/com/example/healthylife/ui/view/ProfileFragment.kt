package com.example.healthylife.ui.view

import android.content.res.ColorStateList
import android.graphics.Outline
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import com.example.healthylife.R
import com.example.healthylife.data.DummyData
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.databinding.FragmentProfileBinding
import com.example.healthylife.model.User
import com.example.healthylife.ui.view.widget.CropImageView
import com.example.healthylife.util.AvatarStore
import com.example.healthylife.util.ThemePrefs

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { HealthRepository(requireContext().applicationContext) }

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) showCropDialog(uri)
    }

    private fun showCropDialog(uri: Uri) {
        val bmp = AvatarStore.decode(requireContext(), uri) ?: return
        val crop = CropImageView(requireContext()).apply { setBitmap(bmp) }
        val density = resources.displayMetrics.density
        val sizePx = (300 * density).toInt()
        val pad = (16 * density).toInt()

        val rotateBtn = android.widget.TextView(requireContext()).apply {
            text = "🔄  Putar"
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.bg_chip_unselected)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.app_text_primary))
            setPadding(pad, pad / 2, pad, pad / 2)
            setOnClickListener { crop.rotate90() }
        }

        val column = android.widget.LinearLayout(requireContext()).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
            addView(crop, android.widget.LinearLayout.LayoutParams(sizePx, sizePx).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            })
            addView(rotateBtn, android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = pad })
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Atur Foto")
            .setView(column)
            .setPositiveButton("Simpan") { _, _ ->
                val result = crop.getResult(512)
                if (result != null && AvatarStore.saveBitmap(requireContext(), result)) loadAvatar()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ikon toggle tema
        binding.btnThemeToggle.setImageResource(
            if (ThemePrefs.isDark(requireContext())) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )
        binding.btnThemeToggle.setOnClickListener { ThemePrefs.toggle(requireContext()) }

        binding.btnSettings.setOnClickListener {
            (activity as? Navigator)?.openSettings()
        }

        // Konfigurasi ikon & warna kartu statistik
        setupStat(binding.statAge, "🎂", "Umur", "tahun", R.color.accent_sage)
        setupStat(binding.statHeight, "📏", "Tinggi", "cm", R.color.accent_teal)
        setupStat(binding.statWeight, "⚖️", "Berat", "kg", R.color.health_green)

        // Foto profil: klip lingkaran + pilih gambar
        binding.ivAvatar.clipToOutline = true
        binding.ivAvatar.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(v: View, outline: Outline) {
                outline.setOval(0, 0, v.width, v.height)
            }
        }
        binding.avatarContainer.setOnClickListener {
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        bindUser(repository.getUser(1) ?: DummyData.currentUser)
        loadAvatar()
    }

    private fun loadAvatar() {
        val hasPhoto = AvatarStore.loadInto(binding.ivAvatar)
        binding.ivAvatar.visibility = if (hasPhoto) View.VISIBLE else View.GONE
        binding.tvAvatar.visibility = if (hasPhoto) View.GONE else View.VISIBLE
    }

    private fun setupStat(
        statBinding: com.example.healthylife.databinding.ItemProfileStatBinding,
        emoji: String, label: String, unit: String, colorRes: Int
    ) {
        val color = ContextCompat.getColor(requireContext(), colorRes)
        statBinding.tvIcon.text = emoji
        statBinding.tvIcon.backgroundTintList =
            ColorStateList.valueOf(ColorUtils.setAlphaComponent(color, 38))
        statBinding.tvUnit.text = unit
        statBinding.tvUnit.setTextColor(color)
        statBinding.tvLabel.text = label
    }

    private fun bindUser(user: User) {
        binding.tvAvatar.text = user.name.take(1).uppercase()
        binding.tvName.text = user.name

        binding.statAge.tvValue.text = user.age.toString()
        binding.statHeight.tvValue.text = user.height.toInt().toString()
        binding.statWeight.tvValue.text = user.weight.toInt().toString()

        val bmi = if (user.height > 0f) user.weight / ((user.height / 100f) * (user.height / 100f)) else 0f
        binding.tvBmi.text = String.format("%.1f", bmi)

        val (statusText, statusColorRes) = when {
            bmi < 18.5 -> "Kurus" to R.color.accent_teal
            bmi < 25f  -> "Normal ✓" to R.color.health_green
            bmi < 30f  -> "Gemuk" to R.color.card_pink
            else       -> "Obesitas" to R.color.card_pink
        }
        val statusColor = ContextCompat.getColor(requireContext(), statusColorRes)
        binding.tvBmiStatus.text = statusText
        binding.tvBmiStatus.setTextColor(statusColor)
        binding.tvBmiStatus.backgroundTintList =
            ColorStateList.valueOf(ColorUtils.setAlphaComponent(statusColor, 38))

        binding.progressBmi.progress = (((bmi - 15f) / 25f) * 100f).coerceIn(0f, 100f).toInt()

        binding.tvTargetCalories.text = "🍽️  Kalori: ${user.targetCalories} cal"
        binding.tvTargetSleep.text = "🌙  Tidur: ${user.targetSleepHours.toInt()} jam"
        binding.tvTargetExercise.text = "💪  Olahraga: ${user.targetExerciseMinutes} menit"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
