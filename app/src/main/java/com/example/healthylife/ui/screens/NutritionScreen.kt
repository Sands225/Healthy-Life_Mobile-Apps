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
import com.example.healthylife.ui.theme.*

@Composable
fun NutritionScreen(padding: PaddingValues) {

    var search by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf("Medium") }

    val recentFoods = listOf(
        Triple("Nasi Goreng",  "450 kcal", CardOrange),
        Triple("Mie Gacoan",   "380 kcal", CardPink),
        Triple("Ayam Geprek",  "520 kcal", CardYellow),
        Triple("Kopi Kenangan","120 kcal", SkyBlue)
    )

    val mealTypes = listOf(
        Triple("🥗", "Light",  "~250 kcal"),
        Triple("🍛", "Medium", "~500 kcal"),
        Triple("🍔", "Heavy",  "~800 kcal")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        // ── Header ────────────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF1A0A18), DeepNavy))
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Text("Nutrition", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Track your daily intake", color = TextSecondary, fontSize = 13.sp)
                }
            }
        }

        // ── Calorie Progress Card ─────────────────────────────────────────────
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
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("Calories Today", color = TextSecondary, fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("1,200", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                                Text(" / 2,000 kcal", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                        Text("60%", color = HealthGreen, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { 0.6f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = HealthGreen,
                        trackColor = SlateLight
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("🥦 Carbs 60%", color = TextMuted, fontSize = 11.sp)
                        Text("🥩 Protein 25%", color = TextMuted, fontSize = 11.sp)
                        Text("🧈 Fat 15%", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }

        // ── Meal Type Selection ───────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Meal Type",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                mealTypes.forEach { (emoji, label, kcal) ->
                    val isSelected = selectedMeal == label
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) HealthGreen.copy(0.15f) else Slate)
                            .border(
                                1.dp,
                                if (isSelected) HealthGreen else SlateLight,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedMeal = label }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 22.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                label,
                                color = if (isSelected) HealthGreen else TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                            Text(kcal, color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // ── Search Field ──────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("Search food...", color = TextMuted) },
                label = { Text("Search Food") },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = TextMuted)
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor    = HealthGreen,
                    unfocusedBorderColor  = SlateLight,
                    focusedLabelColor     = HealthGreen,
                    unfocusedLabelColor   = TextSecondary,
                    cursorColor           = HealthGreen,
                    focusedTextColor      = TextPrimary,
                    unfocusedTextColor    = TextPrimary,
                    unfocusedContainerColor = Slate,
                    focusedContainerColor   = Slate
                )
            )
        }

        // ── Recent Items Label ────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Recent Items",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(12.dp))
        }

        // ── Recent Food List ──────────────────────────────────────────────────
        items(recentFoods) { (name, kcal, accentColor) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Slate)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍽️", fontSize = 18.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text(kcal, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = HealthGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}