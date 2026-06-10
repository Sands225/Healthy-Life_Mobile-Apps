package com.example.healthylife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.healthylife.ui.theme.*

@Composable
fun SleepScreen(padding: PaddingValues) {

    var selected by remember { mutableStateOf("Excellent") }

    val qualityOptions = listOf(
        Triple("😴", "Excellent", HealthGreen),
        Triple("🙂", "Normal",    SkyBlue),
        Triple("🥱", "Poor",      CardPink)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
    ) {

        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF1A0A2E), DeepNavy))
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Text("Sleep Tracker", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Monitor your sleep quality", color = TextSecondary, fontSize = 13.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {

            // ── Sleep Summary Card ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF1A0A2E), Color(0xFF0C1A2E)))
                    )
                    .border(1.dp, SoftPurple.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column {

                    // Card label
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🌙", fontSize = 20.sp)
                        Text(
                            "Last Night's Sleep",
                            color = SoftPurple,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    // Big hours number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("8", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 56.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "hrs",
                            color = TextSecondary,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    HorizontalDivider(color = SlateLight, thickness = 1.dp)
                    Spacer(Modifier.height(20.dp))

                    // Timeline
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SleepTimeItem("🌙", "Bed Time", "22:00")

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(2.dp)
                                    .background(
                                        Brush.linearGradient(listOf(SoftPurple, SkyBlue))
                                    )
                            )
                            Spacer(Modifier.height(2.dp))
                            Text("8 hrs", color = TextMuted, fontSize = 10.sp)
                        }

                        SleepTimeItem("☀️", "Wake Time", "06:00")
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Quality Label ─────────────────────────────────────────────────
            Text(
                "Sleep Quality",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(14.dp))

            // ── Quality Chips ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(Modifier.weight(1f))

            // ── Save Button ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(SoftPurple, SkyBlue)))
                    .clickable { }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Save Sleep Log",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun SleepTimeItem(emoji: String, label: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 24.sp)
        Spacer(Modifier.height(4.dp))
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Text(time, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}