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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthylife.data.DummyData
import com.example.healthylife.ui.theme.*

private data class ActivityItem(
    val emoji: String,
    val name: String,
    val caloriesPerMin: Int
)

@Composable
fun ExerciseScreen(padding: PaddingValues) {

    var duration by remember { mutableFloatStateOf(30f) }
    var selectedActivity by remember { mutableStateOf("Running") }
    var logSaved by remember { mutableStateOf(false) }

    val activities = listOf(
        ActivityItem("🏃", "Running",  10),
        ActivityItem("🚶", "Walking",  4),
        ActivityItem("🧘", "Yoga",     4),
        ActivityItem("🏋️", "Gym",     9),
        ActivityItem("🚴", "Cycling",  8),
        ActivityItem("🏊", "Swimming", 11)
    )

    val selectedItem = activities.find { it.name == selectedActivity } ?: activities[0]
    val estimatedCals = (duration * selectedItem.caloriesPerMin).toInt()

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
                    ExerciseStat("⏱️", "${DummyData.todayExerciseMinutes}", "menit", "Durasi Hari Ini", HealthGreen)
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(SlateLight)
                    )
                    ExerciseStat("🔥", "${DummyData.todayCaloriesBurned}", "kcal", "Kalori Terbakar", AccentTeal)
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(SlateLight)
                    )
                    ExerciseStat("💪", "${DummyData.todayExercises.size}", "sesi", "Sesi Hari Ini", AccentSage)
                }
            }
        }

        // ── Activity Selection Grid ────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Pilih Aktivitas",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .height(200.dp)
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
            Text(
                "Riwayat Olahraga",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(12.dp))
        }

        items(DummyData.exercises) { ex ->
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

        // ── Save Button ───────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (logSaved)
                            Brush.linearGradient(listOf(AccentTeal, HealthGreenDark))
                        else
                            Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))
                    )
                    .clickable { logSaved = !logSaved }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        if (logSaved) Icons.Default.CheckCircle else Icons.Default.Done,
                        null,
                        tint = DeepNavy
                    )
                    Text(
                        if (logSaved) "Log Tersimpan! ✓" else "Simpan Log Olahraga",
                        color = DeepNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
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