package com.example.healthylife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.healthylife.ui.theme.*

private data class ActivityItem(
    val emoji: String,
    val name: String,
    val gradient: List<Color>
)

@Composable
fun ExerciseScreen(padding: PaddingValues) {

    var duration by remember { mutableFloatStateOf(30f) }
    var selectedActivity by remember { mutableStateOf("Running") }

    val activities = listOf(
        ActivityItem("🏃", "Running",  listOf(CardOrange, Color(0xFFFF9500))),
        ActivityItem("🚶", "Walking",  listOf(HealthGreen, SkyBlue)),
        ActivityItem("🧘", "Yoga",     listOf(SoftPurple, Color(0xFF7C3AED))),
        ActivityItem("🏋️", "Gym",     listOf(CardPink,   Color(0xFFC026D3)))
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
                    Brush.verticalGradient(listOf(Color(0xFF1A0A00), DeepNavy))
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Text("Exercise Log", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Track your daily activity", color = TextSecondary, fontSize = 13.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {

            // ── Activity Grid ─────────────────────────────────────────────────
            Text(
                "Choose Activity",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.8.sp
            )
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(190.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                items(activities) { item ->
                    val isSelected = selectedActivity == item.name
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) Brush.linearGradient(item.gradient)
                                else Brush.linearGradient(listOf(Slate, Slate))
                            )
                            .then(
                                if (!isSelected) Modifier.border(1.dp, SlateLight, RoundedCornerShape(16.dp))
                                else Modifier
                            )
                            .clickable { selectedActivity = item.name }
                            .padding(horizontal = 16.dp, vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(item.emoji, fontSize = 28.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                item.name,
                                color = Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Duration Card ─────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Slate)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Duration", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text(
                            "${duration.toInt()} min",
                            color = HealthGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
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
                        Text("15 min", color = TextMuted, fontSize = 11.sp)
                        Text("120 min", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── Estimated Calories ────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A0A))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CardOrange.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔥", fontSize = 20.sp)
                    }
                    Column {
                        Text("Estimated Calories", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            "~${(duration * 5f).toInt()} kcal",
                            color = CardOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Gradient Save Button ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(HealthGreen, SkyBlue)))
                    .clickable { }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Done, null, tint = DeepNavy)
                    Text("Save Log", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}