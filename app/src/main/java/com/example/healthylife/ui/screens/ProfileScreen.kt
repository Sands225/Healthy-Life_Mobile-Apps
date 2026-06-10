package com.example.healthylife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthylife.data.DummyData
import com.example.healthylife.ui.theme.*

@Composable
fun ProfileScreen(padding: PaddingValues) {

    val user = DummyData.currentUser
    val bmi = user.weight / ((user.height / 100f) * (user.height / 100f))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
    ) {

        // ── Header / Avatar Banner ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF0A2218), DeepNavy))
                )
                .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.first().toString(),
                        color = DeepNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                }
                Spacer(Modifier.height(14.dp))
                Text(user.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Healthy Life Member 🌿", color = HealthGreen, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                // Streak badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HealthGreen.copy(0.15f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        "🔥 ${user.streakDays} Hari Streak",
                        color = HealthGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {

            // ── Stats Row ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(
                    modifier   = Modifier.weight(1f),
                    icon       = Icons.Default.Cake,
                    label      = "Umur",
                    value      = "${user.age}",
                    unit       = "tahun",
                    accentColor = AccentSage
                )
                ProfileStatCard(
                    modifier   = Modifier.weight(1f),
                    icon       = Icons.Default.Height,
                    label      = "Tinggi",
                    value      = "${user.height.toInt()}",
                    unit       = "cm",
                    accentColor = AccentTeal
                )
                ProfileStatCard(
                    modifier   = Modifier.weight(1f),
                    icon       = Icons.Default.MonitorWeight,
                    label      = "Berat",
                    value      = "${user.weight.toInt()}",
                    unit       = "kg",
                    accentColor = HealthGreen
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── BMI Card ──────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Slate)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("BMI Index", color = TextSecondary, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            String.format("%.1f", bmi),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                        val bmiLabel = when {
                            bmi < 18.5 -> "Kurus"
                            bmi < 25f  -> "Normal ✓"
                            bmi < 30f  -> "Gemuk"
                            else       -> "Obesitas"
                        }
                        val bmiColor = when {
                            bmi < 18.5 -> AccentTeal
                            bmi < 25f  -> HealthGreen
                            else       -> CardPink
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(bmiColor.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                bmiLabel,
                                color = bmiColor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { ((bmi - 15f) / 25f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = HealthGreen,
                        trackColor = SlateLight
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Kurus", color = TextMuted, fontSize = 10.sp)
                        Text("Normal", color = HealthGreen, fontSize = 10.sp)
                        Text("Gemuk", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // ── Edit Profile Button ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    Icon(Icons.Default.Edit, null, tint = DeepNavy)
                    Text("Edit Profil", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun ProfileStatCard(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    unit: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(unit, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Text(label, color = TextSecondary, fontSize = 11.sp)
        }
    }
}