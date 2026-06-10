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
fun NutritionScreen(padding: PaddingValues) {

    var search by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf("Semua") }

    val mealTypes = listOf(
        Triple("🥗", "Semua",  ""),
        Triple("🥣", "Breakfast", ""),
        Triple("🍛", "Lunch", ""),
        Triple("🍽️", "Dinner", ""),
        Triple("🍎", "Snack", "")
    )

    val filteredFoods = DummyData.foods.filter { food ->
        val matchMeal = selectedMeal == "Semua" || food.mealType == selectedMeal
        val matchSearch = search.isEmpty() || food.name.contains(search, ignoreCase = true)
        matchMeal && matchSearch
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
                Column {
                    Text("Nutrisi", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Pantau asupan harianmu", color = TextSecondary, fontSize = 13.sp)
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
                            Text("Kalori Hari Ini", color = TextSecondary, fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "${DummyData.totalCaloriesToday}",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 30.sp
                                )
                                Text(
                                    " / ${DummyData.currentUser.targetCalories} kcal",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Text(
                            "${((DummyData.totalCaloriesToday.toFloat() / DummyData.currentUser.targetCalories) * 100).toInt()}%",
                            color = HealthGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { (DummyData.totalCaloriesToday.toFloat() / DummyData.currentUser.targetCalories).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = HealthGreen,
                        trackColor = SlateLight
                    )
                    Spacer(Modifier.height(14.dp))
                    // Macros
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MacroStat("🥦 Karbo", "${DummyData.totalCarbsToday.toInt()}g", HealthGreen)
                        MacroStat("🥩 Protein", "${DummyData.totalProteinToday.toInt()}g", AccentTeal)
                        MacroStat("🧈 Lemak", "${DummyData.totalFatToday.toInt()}g", AccentSage)
                    }
                }
            }
        }

        // ── Meal Filter ───────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Filter Makanan",
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mealTypes.forEach { (emoji, label, _) ->
                    val isSelected = selectedMeal == label
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) HealthGreen.copy(0.18f) else Slate)
                            .border(
                                1.dp,
                                if (isSelected) HealthGreen else SlateLight,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedMeal = label }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 18.sp)
                            Spacer(Modifier.height(3.dp))
                            Text(
                                label,
                                color = if (isSelected) HealthGreen else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        // ── Search Field ──────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("Cari makanan...", color = TextMuted) },
                label = { Text("Cari Makanan") },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = TextMuted)
                },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        Icon(
                            Icons.Default.Clear,
                            null,
                            tint = TextMuted,
                            modifier = Modifier.clickable { search = "" }
                        )
                    }
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

        // ── Food List ─────────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Daftar Makanan",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    "${filteredFoods.size} item",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        if (filteredFoods.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔍", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Makanan tidak ditemukan", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
        }

        items(filteredFoods) { food ->
            var added by remember { mutableStateOf(false) }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (added) HealthGreenMuted else Slate
                ),
                border = if (added) androidx.compose.foundation.BorderStroke(1.dp, HealthGreen.copy(0.4f)) else null
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(HealthGreen.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(food.emoji, fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(food.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(
                            "${food.calories} kcal  ·  K:${food.carbs.toInt()}g  P:${food.protein.toInt()}g  L:${food.fat.toInt()}g",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(food.mealType, color = AccentTeal, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (added) HealthGreen else HealthGreen.copy(0.15f))
                            .clickable { added = !added },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (added) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "Tambah",
                            tint = if (added) DeepNavy else HealthGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = TextMuted, fontSize = 10.sp)
    }
}