package com.example.healthylife.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthylife.R
import com.example.healthylife.data.DummyData
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.databinding.DialogEditSleepBinding
import com.example.healthylife.databinding.FragmentSleepBinding
import com.example.healthylife.model.SleepRecord
import com.example.healthylife.model.User
import com.example.healthylife.ui.view.adapter.SleepAdapter
import com.example.healthylife.ui.view.widget.AnalyticsBinder
import com.example.healthylife.ui.view.widget.Segmented
import com.example.healthylife.util.DateUtils
import com.example.healthylife.util.ThemePrefs
import com.example.healthylife.util.TimeFilter

class SleepFragment : Fragment() {

    private var _binding: FragmentSleepBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { HealthRepository(requireContext().applicationContext) }
    private var user: User = DummyData.currentUser
    private var records: List<SleepRecord> = DummyData.sleepRecords

    private lateinit var adapter: SleepAdapter
    private lateinit var analytics: AnalyticsBinder
    private lateinit var filter: Segmented
    private lateinit var qualitySeg: Segmented

    private val qualities = listOf("Baik", "Cukup", "Buruk")
    private var hoursInput = 8f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSleepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnThemeToggle.setImageResource(
            if (ThemePrefs.isDark(requireContext())) R.drawable.ic_light_mode else R.drawable.ic_dark_mode
        )
        binding.btnThemeToggle.setOnClickListener { ThemePrefs.toggle(requireContext()) }

        adapter = SleepAdapter(emptyList(), onEdit = ::showEditDialog, onDelete = ::confirmDelete)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        analytics = AnalyticsBinder(
            binding.analytics,
            unit = "jam",
            accentColor = color(R.color.accent_teal),
            trackColor = color(R.color.app_slate_light),
            labelColor = color(R.color.app_text_muted),
            stateKey = "sleep"
        )

        filter = Segmented(
            listOf(binding.filter.chipToday, binding.filter.chipWeek, binding.filter.chipAll),
            initial = 1
        ) { renderList() }

        qualitySeg = Segmented(
            listOf(binding.chipBaik, binding.chipCukup, binding.chipBuruk),
            initial = 0
        ) { }

        binding.seekHours.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                hoursInput = progress / 10f
                binding.tvHoursValue.text = "${String.format("%.1f", hoursInput)} jam"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        binding.btnSave.setOnClickListener {
            repository.insertSleepRecord(
                SleepRecord(0, DateUtils.getTodayDateString(), "", "", hoursInput, qualities[qualitySeg.selected])
            )
            loadData(); renderAll()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData(); renderAll()
    }

    private fun loadData() {
        repository.getUser(1)?.let { user = it }
        records = repository.getAllSleepRecords().ifEmpty { DummyData.sleepRecords }
    }

    private fun renderAll() {
        val last = records.firstOrNull { DateUtils.isToday(it.date) } ?: records.firstOrNull() ?: DummyData.lastNightSleep
        binding.tvLastHours.text = "${last.durationHours.toInt()}"
        val avg = if (records.isNotEmpty()) records.map { it.durationHours }.average().toFloat() else 8f
        binding.tvAvgHours.text = "${String.format("%.1f", avg)} jam"
        binding.tvTargetHours.text = "${user.targetSleepHours.toInt()} jam"

        analytics.setData(records.map { it.date to it.durationHours })
        renderList()
    }

    private fun renderList() {
        val mode = TimeFilter.values()[filter.selected]
        val filtered = records.filter { mode.matches(it.date) }
        adapter.submit(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.recycler.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun showEditDialog(rec: SleepRecord) {
        val dialogBinding = DialogEditSleepBinding.inflate(layoutInflater)
        dialogBinding.etHours.setText(rec.durationHours.toString())
        val seg = Segmented(
            listOf(dialogBinding.chipBaik, dialogBinding.chipCukup, dialogBinding.chipBuruk),
            initial = qualities.indexOf(rec.quality).coerceAtLeast(0)
        ) { }
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Log Tidur")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val h = (dialogBinding.etHours.text.toString().toFloatOrNull() ?: rec.durationHours).coerceIn(0f, 24f)
                repository.updateSleepRecord(rec.copy(durationHours = h, quality = qualities[seg.selected]))
                loadData(); renderAll()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun confirmDelete(rec: SleepRecord) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Log Tidur")
            .setMessage("Hapus catatan tidur ${DateUtils.toRelativeString(rec.date)}?")
            .setPositiveButton("Hapus") { _, _ ->
                repository.deleteSleepRecord(rec.id); loadData(); renderAll()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun color(res: Int) = ContextCompat.getColor(requireContext(), res)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
