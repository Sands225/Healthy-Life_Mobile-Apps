package com.example.healthylife.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthylife.data.DummyData
import com.example.healthylife.ui.theme.*

@Composable
fun HomeScreen(padding: PaddingValues) {

    val user = DummyData.currentUser
    val sleep = DummyData.lastNightSleep
    val totalCalories = DummyData.totalCaloriesToday
    val exerciseMinutes = DummyData.todayExerciseMinutes

    // Animasi progress
    var showProgress by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { showProgress = true }

    val sleepProgress by animateFloatAsState(
        targetValue = if (showProgress) (sleep.durationHours / user.targetSleepHours).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(1200), label = "sleep"
    )
    val caloriesProgress by animateFloatAsState(
        targetValue = if (showProgress) (totalCalories.toFloat() / user.targetCalories).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(1200), label = "calories"
    )
    val exerciseProgress by animateFloatAsState(
        targetValue = if (showProgress) (exerciseMinutes.toFloat() / user.targetExerciseMinutes).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(1200), label = "exercise"
    )

    // Dialog states untuk quick-add
    var showSleepDialog by remember { mutableStateOf(false) }
    var showNutritionDialog by remember { mutableStateOf(false) }
    var showExerciseDialog by remember { mutableStateOf(false) }

    // Quick-add state
    var sleepHoursInput by remember { mutableStateOf("8") }
    var caloriesInput by remember { mutableStateOf("") }
    var exerciseMinsInput by remember { mutableStateOf("30") }

    // Dialog: Tambah Tidur
    if (showSleepDialog) {
        AlertDialog(
            onDismissRequest = { showSleepDialog = false },
            containerColor = Slate,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🌙", fontSize = 22.sp)
                    Text("Log Tidur", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Berapa jam kamu tidur malam ini?", color = TextSecondary, fontSize = 14.sp)
                    OutlinedTextField(
                        value = sleepHoursInput,
                        onValueChange = { sleepHoursInput = it },
                        label = { Text("Jam Tidur") },
                        suffix = { Text("jam", color = TextMuted) },
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
                            unfocusedContainerColor = SlateLight,
                            focusedContainerColor = SlateLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    // Preset buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("6", "7", "8", "9").forEach { h ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (sleepHoursInput == h) HealthGreen else SlateLighter)
                                    .clickable { sleepHoursInput = h }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$h j",
                                    color = if (sleepHoursInput == h) DeepNavy else TextSecondary,
                                    fontSize = 13.sp, fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HealthGreen)
                        .clickable { showSleepDialog = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Simpan", color = DeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSleepDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // Dialog: Tambah Nutrisi
    if (showNutritionDialog) {
        AlertDialog(
            onDismissRequest = { showNutritionDialog = false },
            containerColor = Slate,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🍽️", fontSize = 22.sp)
                    Text("Log Makanan", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Pilih makanan yang kamu makan:", color = TextSecondary, fontSize = 14.sp)
                    DummyData.foods.take(5).forEach { food ->
                        val isSelected = caloriesInput == food.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) HealthGreenMuted else SlateLighter)
                                .border(
                                    1.dp,
                                    if (isSelected) HealthGreen else SlateLight,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { caloriesInput = food.name }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(food.emoji, fontSize = 20.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(food.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("${food.calories} kcal", color = AccentTeal, fontSize = 11.sp)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, null, tint = HealthGreen, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HealthGreen)
                        .clickable { showNutritionDialog = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Tambah", color = DeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNutritionDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    // Dialog: Tambah Olahraga
    if (showExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showExerciseDialog = false },
            containerColor = Slate,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("💪", fontSize = 22.sp)
                    Text("Log Olahraga", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Berapa menit kamu olahraga?", color = TextSecondary, fontSize = 14.sp)
                    OutlinedTextField(
                        value = exerciseMinsInput,
                        onValueChange = { exerciseMinsInput = it },
                        label = { Text("Durasi") },
                        suffix = { Text("menit", color = TextMuted) },
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
                            unfocusedContainerColor = SlateLight,
                            focusedContainerColor = SlateLight
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("15", "30", "45", "60").forEach { m ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (exerciseMinsInput == m) HealthGreen else SlateLighter)
                                    .clickable { exerciseMinsInput = m }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${m}m",
                                    color = if (exerciseMinsInput == m) DeepNavy else TextSecondary,
                                    fontSize = 13.sp, fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(HealthGreen)
                        .clickable { showExerciseDialog = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Simpan", color = DeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExerciseDialog = false }) {
                    Text("Batal", color = TextSecondary)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {

        // ── Header ───────────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0A2218), DeepNavy)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Selamat Pagi 👋",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = user.name,
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Tetap sehat hari ini! 🌿",
                            color = HealthGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    // Theme Toggle and Avatar Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val isDark = LocalDarkTheme.current
                        val toggleTheme = LocalThemeToggle.current
                        
                        IconButton(
                            onClick = toggleTheme,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GlassWhite)
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Ubah Tema",
                                tint = HealthGreen
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.first().toString(),
                                color = DeepNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                    }
                }
            }
        }

        // ── Streak Card ───────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🔥 ${user.streakDays} Hari Berturut",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Pertahankan kebiasaan sehatmu!",
                            color = Color.White.copy(alpha = 0.80f),
                            fontSize = 13.sp
                        )
                    }
                    Text(text = "🏆", fontSize = 40.sp)
                }
            }
        }

        // ── Progress Ring Section ─────────────────────────────────────────────
        item {
            Spacer(Modifier.height(28.dp))
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress Hari Ini",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProgressRingCard(
                    label = "Kalori",
                    value = "$totalCalories",
                    unit = "kcal",
                    progress = caloriesProgress,
                    color = HealthGreen,
                    emoji = "🍽️"
                )
                ProgressRingCard(
                    label = "Olahraga",
                    value = "$exerciseMinutes",
                    unit = "menit",
                    progress = exerciseProgress,
                    color = AccentTeal,
                    emoji = "💪"
                )
                ProgressRingCard(
                    label = "Tidur",
                    value = "${sleep.durationHours.toInt()}",
                    unit = "jam",
                    progress = sleepProgress,
                    color = AccentSage,
                    emoji = "🌙"
                )
            }
        }

        // ── Quick Add Section ─────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(28.dp))
            Text(
                text = "Tambah Cepat",
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAddCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🌙",
                    label = "Tidur",
                    sublabel = "${sleep.durationHours.toInt()} jam",
                    color = AccentSage,
                    onClick = { showSleepDialog = true }
                )
                QuickAddCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🍽️",
                    label = "Makanan",
                    sublabel = "${totalCalories} kcal",
                    color = HealthGreen,
                    onClick = { showNutritionDialog = true }
                )
                QuickAddCard(
                    modifier = Modifier.weight(1f),
                    emoji = "💪",
                    label = "Olahraga",
                    sublabel = "${exerciseMinutes} mnt",
                    color = AccentTeal,
                    onClick = { showExerciseDialog = true }
                )
            }
        }

        // ── Aktivitas Hari Ini ────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(28.dp))
            Text(
                text = "Aktivitas Hari Ini",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(12.dp))
        }

        items(DummyData.todayExercises) { ex ->
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
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(HealthGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(ex.emoji, fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(ex.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("${ex.durationMinutes} menit · ${ex.caloriesBurned} kcal terbakar", color = TextSecondary, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HealthGreen.copy(0.12f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("Selesai ✓", color = HealthGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // ── Makanan Hari Ini ──────────────────────────────────────────────────
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
                    text = "Makanan Hari Ini",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "${totalCalories} / ${user.targetCalories} kcal",
                    color = AccentTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(12.dp))
            // Macro bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate)
            ) {
                Column(Modifier.padding(16.dp)) {
                    LinearProgressIndicator(
                        progress = { (totalCalories.toFloat() / user.targetCalories).coerceIn(0f, 1f) },
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
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MacroChip("Karbo", "${DummyData.totalCarbsToday.toInt()}g", HealthGreen)
                        MacroChip("Protein", "${DummyData.totalProteinToday.toInt()}g", AccentTeal)
                        MacroChip("Lemak", "${DummyData.totalFatToday.toInt()}g", AccentSage)
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }

        items(DummyData.todayFoods.take(3)) { food ->
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
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(HealthGreen.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(food.emoji, fontSize = 18.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(food.name, color = TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text("${food.calories} kcal · ${food.mealType}", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // ── Smart Insight ─────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(28.dp))
            Text(
                text = "Insight Mingguan",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(14.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(DummyData.smartInsights) { insight ->
                    Card(
                        modifier = Modifier.width(220.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(HealthGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = HealthGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = insight,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Progress Ring Card ────────────────────────────────────────────────────────
@Composable
private fun ProgressRingCard(
    label: String,
    value: String,
    unit: String,
    progress: Float,
    color: Color,
    emoji: String,
    ringSize: Dp = 82.dp,
    strokeWidth: Float = 8f
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(ringSize)
                .drawBehind {
                    val stroke = strokeWidth
                    val sweepAngle = 360f * progress
                    // Background track
                    drawArc(
                        color = Color(0xFF1F3828),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                        topLeft = Offset(stroke / 2, stroke / 2),
                        size = Size(size.width - stroke, size.height - stroke)
                    )
                    // Progress arc
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                        topLeft = Offset(stroke / 2, stroke / 2),
                        size = Size(size.width - stroke, size.height - stroke)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(emoji, fontSize = 16.sp)
                Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(unit, color = color, fontSize = 9.sp, fontWeight = FontWeight.Medium)
            }
        }
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

// ── Quick Add Card ─────────────────────────────────────────────────────────────
@Composable
private fun QuickAddCard(
    modifier: Modifier,
    emoji: String,
    label: String,
    sublabel: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 18.sp)
            }
            Text(label, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            Text(sublabel, color = color, fontSize = 11.sp)
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
                    .padding(4.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Tambah $label",
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// ── Macro Chip ────────────────────────────────────────────────────────────────
@Composable
private fun MacroChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(label, color = TextMuted, fontSize = 10.sp)
    }
}