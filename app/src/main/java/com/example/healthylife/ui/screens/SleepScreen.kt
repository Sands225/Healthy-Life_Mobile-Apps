package com.example.healthylife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun SleepScreen(padding: PaddingValues) {

    val sleepRecord = DummyData.lastNightSleep
    val allRecords = DummyData.sleepRecords

    var selected by remember { mutableStateOf(sleepRecord.quality) }
    var bedTime by remember { mutableStateOf(sleepRecord.bedTime) }
    var wakeTime by remember { mutableStateOf(sleepRecord.wakeTime) }

    val qualityOptions = listOf(
        Triple("😴", "Excellent", HealthGreen),
        Triple("🙂", "Normal",    AccentTeal),
        Triple("🥱", "Poor",      CardPink)
    )

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
                        Text("Sleep Tracker", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
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

        // ── Sleep Summary Card ─────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF0F2A1E), Color(0xFF0D1F18)))
                    )
                    .border(1.dp, HealthGreen.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🌙", fontSize = 20.sp)
                        Text(
                            "Tidur Semalam",
                            color = HealthGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HealthGreen.copy(0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(sleepRecord.quality, color = HealthGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            "${sleepRecord.durationHours.toInt()}",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 60.sp
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "jam",
                            color = TextSecondary,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SleepStatItem("🌙", "Tidur", sleepRecord.bedTime)
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(2.dp)
                                .background(Brush.linearGradient(listOf(HealthGreen, AccentTeal)))
                                .align(Alignment.CenterVertically)
                        )
                        SleepStatItem("☀️", "Bangun", sleepRecord.wakeTime)
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = SlateLight, thickness = 1.dp)
                    Spacer(Modifier.height(16.dp))

                    // Avg sleep
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Rata-rata Minggu Ini", color = TextMuted, fontSize = 11.sp)
                            Text(
                                "${String.format("%.1f", DummyData.avgSleepHours)} jam",
                                color = AccentTeal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Target Harian", color = TextMuted, fontSize = 11.sp)
                            Text(
                                "${DummyData.currentUser.targetSleepHours.toInt()} jam",
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // ── Weekly Sleep Bar Chart ────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "7 Hari Terakhir",
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        allRecords.reversed().forEachIndexed { i, rec ->
                            val heightFraction = (rec.durationHours / 10f).coerceIn(0f, 1f)
                            val barColor = when (rec.quality) {
                                "Excellent" -> HealthGreen
                                "Normal"    -> AccentTeal
                                else        -> CardPink
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "${rec.durationHours.toInt()}j",
                                    color = barColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height((heightFraction * 80).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (i == allRecords.size - 1)
                                                Brush.verticalGradient(listOf(barColor, barColor.copy(0.5f)))
                                            else
                                                Brush.verticalGradient(listOf(barColor.copy(0.6f), barColor.copy(0.2f)))
                                        )
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    DummyData.weekDayLabels[i],
                                    color = if (i == allRecords.size - 1) HealthGreen else TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = if (i == allRecords.size - 1) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Quality Label ─────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Kualitas Tidur",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                qualityOptions.forEach { (emoji, label, accentColor) ->
                    val isSelected = selected == label
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) accentColor.copy(0.15f) else Slate)
                            .border(
                                1.dp,
                                if (isSelected) accentColor else SlateLight,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selected = label }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 26.sp)
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
        }

        // ── Riwayat Tidur ─────────────────────────────────────────────────────
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
        }

        items(allRecords.drop(1)) { rec ->
            val qColor = when (rec.quality) {
                "Excellent" -> HealthGreen
                "Normal"    -> AccentTeal
                else        -> CardPink
            }
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
                        Text(
                            when (rec.quality) {
                                "Excellent" -> "😴"
                                "Normal" -> "🙂"
                                else -> "🥱"
                            }, fontSize = 18.sp
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(rec.date, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("${rec.bedTime} → ${rec.wakeTime}", color = TextSecondary, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${rec.durationHours.toInt()} jam", color = qColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(rec.quality, color = TextMuted, fontSize = 10.sp)
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
                    .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark)))
                    .clickable { }
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

@Composable
private fun SleepStatItem(emoji: String, label: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Text(time, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}