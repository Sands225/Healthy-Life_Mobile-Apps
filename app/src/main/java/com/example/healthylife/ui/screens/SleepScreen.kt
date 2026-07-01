package com.example.healthylife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthylife.data.DummyData
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.model.SleepRecord
import com.example.healthylife.ui.componenets.AnalyticsSection
import com.example.healthylife.ui.componenets.TimeFilterRow
import com.example.healthylife.ui.theme.*
import com.example.healthylife.util.DateUtils
import com.example.healthylife.util.TimeFilter

private val sleepQualityOptions = listOf(
    Triple("😴", "Baik", "HealthGreen"),
    Triple("🙂", "Cukup", "AccentTeal"),
    Triple("🥱", "Buruk", "CardPink")
)

private fun qualityColor(quality: String): Color = when (quality) {
    "Baik"  -> HealthGreen
    "Cukup" -> AccentTeal
    else    -> CardPink
}

private fun qualityEmoji(quality: String): String = when (quality) {
    "Baik"  -> "😴"
    "Cukup" -> "🙂"
    else    -> "🥱"
}

@Composable
fun SleepScreen(padding: PaddingValues, repository: HealthRepository) {

    var user by remember { mutableStateOf(DummyData.currentUser) }
    val allRecords = remember { mutableStateListOf<SleepRecord>() }

    fun reload() {
        val dbSleep = repository.getAllSleepRecords()
        allRecords.clear()
        allRecords.addAll(dbSleep.ifEmpty { DummyData.sleepRecords })
    }

    LaunchedEffect(Unit) {
        repository.getUser(1)?.let { user = it }
        reload()
    }

    // ── Input log tidur ────────────────────────────────────────────────────────
    var hoursInput by remember { mutableFloatStateOf(8f) }
    var selectedQuality by remember { mutableStateOf("Baik") }

    // ── Filter waktu ───────────────────────────────────────────────────────────
    var timeFilter by remember { mutableStateOf(TimeFilter.MINGGU_INI) }
    val filteredRecords = allRecords.filter { timeFilter.matches(it.date) }

    // ── Dialog edit / hapus ────────────────────────────────────────────────────
    var editingRecord by remember { mutableStateOf<SleepRecord?>(null) }
    var deletingRecord by remember { mutableStateOf<SleepRecord?>(null) }

    editingRecord?.let { rec ->
        EditSleepDialog(
            record = rec,
            onDismiss = { editingRecord = null },
            onConfirm = { updated ->
                repository.updateSleepRecord(updated)
                reload()
                editingRecord = null
            }
        )
    }

    deletingRecord?.let { rec ->
        AlertDialog(
            onDismissRequest = { deletingRecord = null },
            title = { Text("Hapus Log Tidur", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Hapus catatan tidur ${DateUtils.toRelativeString(rec.date)}?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    repository.deleteSleepRecord(rec.id)
                    reload()
                    deletingRecord = null
                }) { Text("Hapus", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deletingRecord = null }) { Text("Batal", color = TextSecondary) }
            },
            containerColor = Slate,
            shape = RoundedCornerShape(20.dp)
        )
    }

    val lastRecord = allRecords.firstOrNull() ?: DummyData.lastNightSleep

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        // ── Header ────────────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(HeaderStart, DeepNavy)))
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Tidur", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Pantau kualitas tidurmu", color = TextSecondary, fontSize = 13.sp)
                    }

                    val isDark = LocalDarkTheme.current
                    val toggleTheme = LocalThemeToggle.current

                    IconButton(
                        onClick = toggleTheme,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(GlassWhite)
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Ubah Tema",
                            tint = HealthGreen
                        )
                    }
                }
            }
        }

        // ── Ringkasan Tidur Semalam ────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(Color(0xFF0F2A1E), Color(0xFF0D1F18))))
                    .border(1.dp, HealthGreen.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🌙", fontSize = 20.sp)
                        Text("Tidur Semalam", color = HealthGreen, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(qualityColor(lastRecord.quality).copy(0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(lastRecord.quality, color = qualityColor(lastRecord.quality), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            "${lastRecord.durationHours.toInt()}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 60.sp
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("jam", color = TextSecondary, fontSize = 20.sp, modifier = Modifier.padding(bottom = 12.dp))
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = SlateLight, thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))

                    val avgSleepHours = if (allRecords.isNotEmpty()) allRecords.map { it.durationHours }.average().toFloat() else 8f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Rata-rata", color = TextMuted, fontSize = 11.sp)
                            Text(
                                "${String.format("%.1f", avgSleepHours)} jam",
                                color = AccentTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Target Harian", color = TextMuted, fontSize = 11.sp)
                            Text(
                                "${user.targetSleepHours.toInt()} jam",
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // ── Form Log: Berapa Jam + Kualitas ────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Catat Tidur",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Slate)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Berapa jam kamu tidur?", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            "${String.format("%.1f", hoursInput)} jam",
                            color = HealthGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Slider(
                        value = hoursInput,
                        onValueChange = { hoursInput = it },
                        valueRange = 0f..24f,
                        colors = SliderDefaults.colors(
                            thumbColor = HealthGreen,
                            activeTrackColor = HealthGreen,
                            inactiveTrackColor = SlateLight
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(6f, 7f, 8f, 9f).forEach { h ->
                            val isP = hoursInput == h
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isP) HealthGreen else SlateLighter)
                                    .clickable { hoursInput = h }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    "${h.toInt()}j",
                                    color = if (isP) DeepNavy else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Text("Kualitas Tidur", color = TextSecondary, fontSize = 13.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        sleepQualityOptions.forEach { (emoji, label, _) ->
                            val isSelected = selectedQuality == label
                            val accentColor = qualityColor(label)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) accentColor.copy(0.15f) else SlateLighter)
                                    .border(
                                        1.dp,
                                        if (isSelected) accentColor else SlateLight,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedQuality = label }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(emoji, fontSize = 24.sp)
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        label,
                                        color = if (isSelected) accentColor else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark)))
                            .clickable {
                                val newRecord = SleepRecord(
                                    id = 0,
                                    date = DateUtils.getTodayDateString(),
                                    bedTime = "",
                                    wakeTime = "",
                                    durationHours = hoursInput,
                                    quality = selectedQuality
                                )
                                repository.insertSleepRecord(newRecord)
                                reload()
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Done, null, tint = DeepNavy)
                            Text("Simpan Log Tidur", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // ── Analitik Tidur ─────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Analitik Tidur",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            AnalyticsSection(
                unit = "jam",
                accent = AccentTeal,
                data = allRecords.map { it.date to it.durationHours }
            )
        }

        // ── Filter + Riwayat ───────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Riwayat Tidur",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            TimeFilterRow(selected = timeFilter, onSelected = { timeFilter = it })
            Spacer(Modifier.height(12.dp))
        }

        if (filteredRecords.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌙", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Belum ada catatan tidur", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
        }

        items(filteredRecords) { rec ->
            val qColor = qualityColor(rec.quality)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(qColor.copy(0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(qualityEmoji(rec.quality), fontSize = 18.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(DateUtils.toRelativeString(rec.date), color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("${rec.durationHours.toInt()} jam · ${rec.quality}", color = TextSecondary, fontSize = 12.sp)
                    }
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opsi", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { showMenu = false; editingRecord = rec },
                                leadingIcon = { Icon(Icons.Default.Edit, null, tint = AccentTeal) }
                            )
                            DropdownMenuItem(
                                text = { Text("Hapus", color = Color.Red) },
                                onClick = { showMenu = false; deletingRecord = rec },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditSleepDialog(
    record: SleepRecord,
    onDismiss: () -> Unit,
    onConfirm: (SleepRecord) -> Unit
) {
    var hoursText by remember { mutableStateOf(record.durationHours.toString()) }
    var quality by remember { mutableStateOf(record.quality) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Log Tidur", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Durasi (jam)", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = hoursText,
                    onValueChange = { hoursText = it.filter { c -> c.isDigit() || c == '.' } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = HealthGreen,
                        unfocusedBorderColor = SlateLight,
                        focusedContainerColor = Slate,
                        unfocusedContainerColor = Slate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Kualitas Tidur", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    sleepQualityOptions.forEach { (emoji, label, _) ->
                        val isSelected = quality == label
                        val accentColor = qualityColor(label)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) accentColor.copy(0.15f) else SlateLighter)
                                .border(1.dp, if (isSelected) accentColor else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { quality = label }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(emoji, fontSize = 18.sp)
                                Text(label, color = if (isSelected) accentColor else TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val hours = (hoursText.toFloatOrNull() ?: record.durationHours).coerceIn(0f, 24f)
                onConfirm(record.copy(durationHours = hours, quality = quality))
            }) { Text("Simpan", color = HealthGreen, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = TextSecondary) }
        },
        containerColor = Slate,
        shape = RoundedCornerShape(20.dp)
    )
}
