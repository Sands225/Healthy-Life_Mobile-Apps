package com.example.healthylife.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.*
import com.example.healthylife.data.DummyData
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.model.User
import com.example.healthylife.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(padding: PaddingValues, repository: HealthRepository) {

    var user by remember { mutableStateOf(DummyData.currentUser) }
    
    LaunchedEffect(Unit) {
        repository.getUser(1)?.let { user = it }
    }

    var showEditProfileSheet by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf("") }
    var editHeight by remember { mutableStateOf("") }
    var editWeight by remember { mutableStateOf("") }
    var editTargetCalories by remember { mutableStateOf("") }
    var editTargetSleepHours by remember { mutableStateOf("") }
    var editTargetExerciseMinutes by remember { mutableStateOf("") }

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
                    Brush.verticalGradient(listOf(HeaderStart, DeepNavy))
                )
                .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            val isDark = LocalDarkTheme.current
            val toggleTheme = LocalThemeToggle.current

            IconButton(
                onClick = toggleTheme,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 20.dp)
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
                Text("Anggota Healthy Life 🌿", color = HealthGreen, fontSize = 13.sp)
            }
        }

    if (showEditProfileSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditProfileSheet = false },
            containerColor = Slate,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                item {
                    Text(
                        "Edit Profil",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Perbarui informasi personal dan target kebugaranmu",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(color = SlateLight)
                }

                item {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = profileTextFieldColors()
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editAge,
                            onValueChange = { editAge = it.filter { c -> c.isDigit() } },
                            label = { Text("Umur (tahun)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = profileTextFieldColors()
                        )
                        OutlinedTextField(
                            value = editHeight,
                            onValueChange = { editHeight = it.filter { c -> c.isDigit() } },
                            label = { Text("Tinggi (cm)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = profileTextFieldColors()
                        )
                        OutlinedTextField(
                            value = editWeight,
                            onValueChange = { editWeight = it },
                            label = { Text("Berat (kg)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = profileTextFieldColors()
                        )
                    }
                }

                item {
                    // Preview BMI langsung dari tinggi & berat yang diedit
                    val h = editHeight.toFloatOrNull() ?: user.height
                    val w = editWeight.toFloatOrNull() ?: user.weight
                    val previewBmi = if (h > 0f) w / ((h / 100f) * (h / 100f)) else 0f
                    val statusLabel = when {
                        previewBmi < 18.5 -> "Kurus"
                        previewBmi < 25f  -> "Normal"
                        previewBmi < 30f  -> "Gemuk"
                        else              -> "Obesitas"
                    }
                    val statusColor = when {
                        previewBmi < 18.5 -> AccentTeal
                        previewBmi < 25f  -> HealthGreen
                        else              -> CardPink
                    }
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateLighter),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("BMI (otomatis)", color = TextSecondary, fontSize = 12.sp)
                                Text(
                                    String.format("%.1f", previewBmi),
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(statusColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(statusLabel, color = statusColor, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            }
                        }
                    }
                }

                item {
                    Text("Target Harian", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editTargetCalories,
                            onValueChange = { editTargetCalories = it.filter { c -> c.isDigit() } },
                            label = { Text("Kalori (kcal)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = profileTextFieldColors()
                        )
                        OutlinedTextField(
                            value = editTargetSleepHours,
                            onValueChange = { editTargetSleepHours = it },
                            label = { Text("Tidur (jam)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = profileTextFieldColors()
                        )
                        OutlinedTextField(
                            value = editTargetExerciseMinutes,
                            onValueChange = { editTargetExerciseMinutes = it.filter { c -> c.isDigit() } },
                            label = { Text("Olahraga (mnt)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = profileTextFieldColors()
                        )
                    }
                }

                item {
                    val canSave = editName.isNotBlank()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (canSave) Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))
                                else Brush.linearGradient(listOf(SlateLight, SlateLight))
                            )
                            .clickable(enabled = canSave) {
                                val updatedUser = User(
                                    id = user.id,
                                    name = editName.trim(),
                                    age = editAge.toIntOrNull() ?: user.age,
                                    weight = editWeight.toFloatOrNull() ?: user.weight,
                                    height = editHeight.toFloatOrNull() ?: user.height,
                                    targetCalories = editTargetCalories.toIntOrNull() ?: user.targetCalories,
                                    targetSleepHours = editTargetSleepHours.toFloatOrNull() ?: user.targetSleepHours,
                                    targetExerciseMinutes = editTargetExerciseMinutes.toIntOrNull() ?: user.targetExerciseMinutes
                                )
                                repository.updateUser(updatedUser)
                                user = updatedUser
                                showEditProfileSheet = false
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Simpan Perubahan",
                            color = if (canSave) DeepNavy else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
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
                    .clickable {
                        editName = user.name
                        editAge = user.age.toString()
                        editHeight = user.height.toInt().toString()
                        editWeight = user.weight.toInt().toString()
                        editTargetCalories = user.targetCalories.toString()
                        editTargetSleepHours = user.targetSleepHours.toString()
                        editTargetExerciseMinutes = user.targetExerciseMinutes.toString()
                        showEditProfileSheet = true
                    }
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

@Composable
private fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
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