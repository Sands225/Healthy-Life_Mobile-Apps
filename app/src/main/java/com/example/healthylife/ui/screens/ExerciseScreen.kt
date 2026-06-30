package com.example.healthylife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
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
import com.example.healthylife.model.Exercise
import com.example.healthylife.ui.theme.*

private data class ActivityItem(
    val emoji: String,
    val name: String,
    val caloriesPerMin: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(padding: PaddingValues) {

    var duration by remember { mutableFloatStateOf(30f) }
    var selectedActivity by remember { mutableStateOf("Running") }
    var logSaved by remember { mutableStateOf(false) }

    // ── State untuk daftar aktivitas (bisa ditambah custom) ──────────────
    val activities = remember {
        mutableStateListOf(
            ActivityItem("🏃", "Running",  10),
            ActivityItem("🚶", "Walking",  4),
            ActivityItem("🧘", "Yoga",     4),
            ActivityItem("🏋️", "Gym",     9),
            ActivityItem("🚴", "Cycling",  8),
            ActivityItem("🏊", "Swimming", 11)
        )
    }

    // ── State untuk riwayat exercise ─────────────────────────────────────
    val exerciseHistory = remember {
        mutableStateListOf(*DummyData.exercises.toTypedArray())
    }

    // ── Bottom Sheet: Tambah Aktivitas ────────────────────────────────────
    var showAddActivitySheet by remember { mutableStateOf(false) }
    var newActivityName by remember { mutableStateOf("") }
    var newActivityEmoji by remember { mutableStateOf("") }
    var newActivityCalPerMin by remember { mutableStateOf("") }

    // ── Bottom Sheet: Tambah ke Database ─────────────────────────────────
    var showSaveToDbSheet by remember { mutableStateOf(false) }
    var saveSuccess by remember { mutableStateOf(false) }

    val selectedItem = activities.find { it.name == selectedActivity } ?: activities[0]
    val estimatedCals = (duration * selectedItem.caloriesPerMin).toInt()

    // ── Bottom Sheet: Tambah Aktivitas Baru ───────────────────────────────
    if (showAddActivitySheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showAddActivitySheet = false
                newActivityName = ""
                newActivityEmoji = ""
                newActivityCalPerMin = ""
            },
            containerColor = Slate,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Tambah Aktivitas",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Buat aktivitas olahraga kustommu",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateLight)
                            .clickable {
                                showAddActivitySheet = false
                                newActivityName = ""
                                newActivityEmoji = ""
                                newActivityCalPerMin = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = SlateLight)

                // Pilih Emoji
                Text("Pilih Ikon Aktivitas", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                val emojiOptions = listOf("🏃", "🚶", "🧘", "🏋️", "🚴", "🏊", "⛷️", "🤸", "🥊", "🏸", "⚽", "🎾", "🧗", "🤽", "🏇")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(130.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false
                ) {
                    items(emojiOptions) { emoji ->
                        val isSelected = newActivityEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) HealthGreen.copy(0.2f) else SlateLighter)
                                .border(1.dp, if (isSelected) HealthGreen else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { newActivityEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(emoji, fontSize = 22.sp)
                        }
                    }
                }

                // Input Nama
                OutlinedTextField(
                    value = newActivityName,
                    onValueChange = { newActivityName = it },
                    label = { Text("Nama Aktivitas") },
                    placeholder = { Text("Contoh: Zumba, Badminton...", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthGreen,
                        unfocusedBorderColor = SlateLight,
                        focusedLabelColor = HealthGreen,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = HealthGreen,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        unfocusedContainerColor = SlateLighter,
                        focusedContainerColor = SlateLighter
                    )
                )

                // Input Kalori per Menit
                OutlinedTextField(
                    value = newActivityCalPerMin,
                    onValueChange = { newActivityCalPerMin = it.filter { c -> c.isDigit() } },
                    label = { Text("Kalori per Menit (kcal)") },
                    placeholder = { Text("Contoh: 7", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HealthGreen,
                        unfocusedBorderColor = SlateLight,
                        focusedLabelColor = HealthGreen,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = HealthGreen,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        unfocusedContainerColor = SlateLighter,
                        focusedContainerColor = SlateLighter
                    )
                )

                // Tombol Simpan
                val canSave = newActivityName.isNotBlank() && newActivityEmoji.isNotEmpty() && newActivityCalPerMin.isNotBlank()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (canSave)
                                Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))
                            else
                                Brush.linearGradient(listOf(SlateLight, SlateLight))
                        )
                        .clickable(enabled = canSave) {
                            activities.add(
                                ActivityItem(
                                    emoji = newActivityEmoji,
                                    name = newActivityName.trim(),
                                    caloriesPerMin = newActivityCalPerMin.toIntOrNull() ?: 5
                                )
                            )
                            selectedActivity = newActivityName.trim()
                            showAddActivitySheet = false
                            newActivityName = ""
                            newActivityEmoji = ""
                            newActivityCalPerMin = ""
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = if (canSave) DeepNavy else TextMuted)
                        Text(
                            "Tambahkan Aktivitas",
                            color = if (canSave) DeepNavy else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }

    // ── Bottom Sheet: Simpan ke Database ──────────────────────────────────
    if (showSaveToDbSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSaveToDbSheet = false
                saveSuccess = false
            },
            containerColor = Slate,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 48.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Simpan ke Database",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Simpan log olahraga ke server",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateLight)
                            .clickable { showSaveToDbSheet = false; saveSuccess = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = SlateLight)

                // Preview data yang akan disimpan
                Text("Data yang Akan Disimpan", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateLighter),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        DbPreviewRow("Aktivitas", "${selectedItem.emoji} ${selectedItem.name}")
                        DbPreviewRow("Durasi", "${duration.toInt()} menit")
                        DbPreviewRow("Kalori Terbakar", "~$estimatedCals kcal")
                        DbPreviewRow("Tanggal", "Hari ini")
                        DbPreviewRow("Pengguna", DummyData.currentUser.name)
                    }
                }

                // Status koneksi database (simulasi)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AccentTeal.copy(0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentTeal.copy(0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(8.dp).clip(CircleShape).background(AccentTeal)
                        )
                        Column {
                            Text("Database Terhubung", color = AccentTeal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Siap menerima data latihan", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                if (saveSuccess) {
                    // Sukses state
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = HealthGreen.copy(0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HealthGreen.copy(0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = HealthGreen, modifier = Modifier.size(28.dp))
                            Column {
                                Text("Berhasil Disimpan!", color = HealthGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Data latihan tersimpan ke database", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                    // Tombol Tutup
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark)))
                            .clickable { showSaveToDbSheet = false; saveSuccess = false; logSaved = true }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Selesai", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                } else {
                    // Tombol Konfirmasi Simpan
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark)))
                            .clickable {
                                // Tambahkan ke riwayat
                                exerciseHistory.add(0,
                                    Exercise(
                                        id = exerciseHistory.size + 1,
                                        name = selectedItem.name,
                                        emoji = selectedItem.emoji,
                                        durationMinutes = duration.toInt(),
                                        caloriesBurned = estimatedCals,
                                        date = "Hari ini"
                                    )
                                )
                                saveSuccess = true
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, null, tint = DeepNavy)
                            Text(
                                "Simpan ke Database",
                                color = DeepNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

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
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF0A2218), DeepNavy))
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Exercise Log", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Catat aktivitas harianmu", color = TextSecondary, fontSize = 13.sp)
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

        // ── Today's Summary ───────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Slate)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val todayLogs = exerciseHistory.filter { it.date == "Hari ini" }
                    ExerciseStat("⏱️", "${todayLogs.sumOf { it.durationMinutes }}", "menit", "Durasi Hari Ini", HealthGreen)
                    Box(modifier = Modifier.width(1.dp).height(50.dp).background(SlateLight))
                    ExerciseStat("🔥", "${todayLogs.sumOf { it.caloriesBurned }}", "kcal", "Kalori Terbakar", AccentTeal)
                    Box(modifier = Modifier.width(1.dp).height(50.dp).background(SlateLight))
                    ExerciseStat("💪", "${todayLogs.size}", "sesi", "Sesi Hari Ini", AccentSage)
                }
            }
        }

        // ── Activity Selection Grid ────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pilih Aktivitas",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                // Tombol Tambah Aktivitas Custom
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(HealthGreen.copy(0.12f))
                        .border(1.dp, HealthGreen.copy(0.3f), RoundedCornerShape(10.dp))
                        .clickable { showAddActivitySheet = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = HealthGreen, modifier = Modifier.size(15.dp))
                        Text("Tambah", color = HealthGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Dynamic Grid (calculated height based on activities count) ─────────
        item {
            val rowCount = Math.ceil(activities.size / 3.0).toInt()
            val gridHeight = (rowCount * 84 + (rowCount - 1) * 10).dp
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .height(gridHeight)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                userScrollEnabled = false
            ) {
                items(activities) { item ->
                    val isSelected = selectedActivity == item.name
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected)
                                    Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))
                                else
                                    Brush.linearGradient(listOf(Slate, Slate))
                            )
                            .then(
                                if (!isSelected) Modifier.border(1.dp, SlateLight, RoundedCornerShape(14.dp))
                                else Modifier
                            )
                            .clickable { selectedActivity = item.name }
                            .padding(horizontal = 10.dp, vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(item.emoji, fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                item.name,
                                color = if (isSelected) DeepNavy else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // ── Duration Card ─────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Slate)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Durasi", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(
                            "${duration.toInt()} menit",
                            color = HealthGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Slider(
                        value = duration,
                        onValueChange = { duration = it },
                        valueRange = 15f..120f,
                        colors = SliderDefaults.colors(
                            thumbColor = HealthGreen,
                            activeTrackColor = HealthGreen,
                            inactiveTrackColor = SlateLight
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("15 mnt", color = TextMuted, fontSize = 11.sp)
                        Text("120 mnt", color = TextMuted, fontSize = 11.sp)
                    }
                    // Quick presets
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15f, 30f, 45f, 60f).forEach { mins ->
                            val isPreset = duration == mins
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isPreset) HealthGreen else SlateLighter)
                                    .clickable { duration = mins }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    "${mins.toInt()}m",
                                    color = if (isPreset) DeepNavy else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Estimated Calories ────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(14.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentTeal.copy(0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AccentTeal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 22.sp)
                    }
                    Column {
                        Text("Estimasi Kalori Terbakar", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            "~$estimatedCals kcal",
                            color = AccentTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${selectedItem.caloriesPerMin} kcal", color = TextMuted, fontSize = 11.sp)
                        Text("per menit", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // ── Riwayat Olahraga ──────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Riwayat Olahraga",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "${exerciseHistory.size} sesi",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        items(exerciseHistory) { ex ->
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
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HealthGreen.copy(0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(ex.emoji, fontSize = 18.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ex.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("${ex.durationMinutes} menit · ${ex.caloriesBurned} kcal", color = TextSecondary, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(ex.date, color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        // ── Action Buttons ────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tombol Simpan ke Database
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark)))
                        .clickable { showSaveToDbSheet = true }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CloudUpload,
                            null,
                            tint = DeepNavy
                        )
                        Text(
                            "Simpan Log & Tambah ke Database",
                            color = DeepNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                // Tombol Log Cepat (tanpa DB)
                if (logSaved) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = HealthGreen.copy(0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HealthGreen.copy(0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = HealthGreen, modifier = Modifier.size(22.dp))
                            Text("Log berhasil disimpan ke database! ✓", color = HealthGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DbPreviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp)
        Text(value, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun ExerciseStat(emoji: String, value: String, unit: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(unit, color = color.copy(0.7f), fontSize = 10.sp)
        Text(label, color = TextMuted, fontSize = 9.sp)
    }
}