package com.example.healthylife.ui.componenets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthylife.data.DummyData
import com.example.healthylife.ui.theme.*
import com.example.healthylife.util.DateUtils
import com.example.healthylife.util.TimeFilter

/**
 * Kartu analitik dengan 3 mode:
 *  - Hari Ini   : ringkasan total hari ini
 *  - Minggu Ini : bar chart per hari (Sen–Min)
 *  - Semua      : bar chart agregasi per minggu (gaya sama dengan Minggu Ini)
 *
 * @param data pasangan (tanggal YYYY-MM-DD, nilai)
 */
@Composable
fun AnalyticsSection(
    unit: String,
    accent: Color,
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    formatValue: (Float) -> String = { it.toInt().toString() }
) {
    var mode by remember { mutableStateOf(TimeFilter.MINGGU_INI) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate)
    ) {
        Column(Modifier.padding(16.dp)) {
            TimeFilterRow(
                selected = mode,
                onSelected = { mode = it },
                horizontalPadding = 0.dp
            )
            Spacer(Modifier.height(16.dp))

            when (mode) {
                TimeFilter.HARI_INI   -> TodaySummary(data, unit, accent, formatValue)
                TimeFilter.MINGGU_INI -> WeeklyBars(data, unit, accent, formatValue)
                TimeFilter.SEMUA      -> AllWeeksBars(data, unit, accent, formatValue)
            }
        }
    }
}

@Composable
private fun TodaySummary(
    data: List<Pair<String, Float>>,
    unit: String,
    accent: Color,
    formatValue: (Float) -> String
) {
    val todayItems = data.filter { DateUtils.isToday(it.first) }
    val total = todayItems.sumOf { it.second.toDouble() }.toFloat()

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Total Hari Ini", color = TextSecondary, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(formatValue(total), color = accent, fontWeight = FontWeight.Bold, fontSize = 40.sp)
            Spacer(Modifier.width(6.dp))
            Text(unit, color = TextSecondary, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text("${todayItems.size} catatan", color = TextMuted, fontSize = 11.sp)
    }
}

@Composable
private fun WeeklyBars(
    data: List<Pair<String, Float>>,
    unit: String,
    accent: Color,
    formatValue: (Float) -> String
) {
    val buckets = FloatArray(7)
    data.forEach { (date, value) ->
        val idx = DateUtils.dayIndexInThisWeek(date)
        if (idx in 0..6) buckets[idx] += value
    }
    BarChart(
        values = buckets.toList(),
        labels = DummyData.weekDayLabels,
        accent = accent,
        formatValue = formatValue,
        footer = "Total minggu ini: ${formatValue(buckets.sum())} $unit"
    )
}

@Composable
private fun AllWeeksBars(
    data: List<Pair<String, Float>>,
    unit: String,
    accent: Color,
    formatValue: (Float) -> String
) {
    // Agregasi per minggu (kunci = tanggal Senin awal minggu)
    val perWeek = sortedMapOf<String, Float>()
    data.forEach { (date, value) ->
        val ws = DateUtils.weekStartString(date) ?: return@forEach
        perWeek[ws] = (perWeek[ws] ?: 0f) + value
    }
    val points = perWeek.entries.toList().takeLast(7)

    if (points.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Belum ada data", color = TextMuted, fontSize = 12.sp)
        }
        return
    }

    BarChart(
        values = points.map { it.value },
        labels = points.map { DateUtils.weekOfYearLabel(it.key) },
        accent = accent,
        formatValue = formatValue,
        footer = null
    )
}

@Composable
private fun BarChart(
    values: List<Float>,
    labels: List<String>,
    accent: Color,
    formatValue: (Float) -> String,
    footer: String? = null
) {
    val maxVal = (values.maxOrNull() ?: 0f).coerceAtLeast(1f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            values.forEachIndexed { i, v ->
                val fraction = (v / maxVal).coerceIn(0f, 1f)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (v > 0f) formatValue(v) else "",
                        color = accent,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height((fraction * 90).dp.coerceAtLeast(3.dp))
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                if (v > 0f)
                                    Brush.verticalGradient(listOf(accent, accent.copy(0.4f)))
                                else
                                    Brush.verticalGradient(listOf(SlateLight, SlateLight))
                            )
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        labels.getOrElse(i) { "" },
                        color = TextMuted,
                        fontSize = 8.sp,
                        maxLines = 1
                    )
                }
            }
        }
        if (footer != null) {
            Spacer(Modifier.height(12.dp))
            Text(footer, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
