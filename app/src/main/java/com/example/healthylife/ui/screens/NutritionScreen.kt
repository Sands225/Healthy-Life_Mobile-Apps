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
import com.example.healthylife.model.Food
import com.example.healthylife.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(padding: PaddingValues) {

    var search by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf("Semua") }

    // ── State untuk daftar makanan (bisa ditambah custom) ─────────────────
    val foodList = remember {
        mutableStateListOf(*DummyData.foods.toTypedArray())
    }

    val mealTypes = listOf(
        Triple("🥗", "Semua",  ""),
        Triple("🥣", "Breakfast", ""),
        Triple("🍛", "Lunch", ""),
        Triple("🍽️", "Dinner", ""),
        Triple("🍎", "Snack", "")
    )

    val filteredFoods = foodList.filter { food ->
        val matchMeal = selectedMeal == "Semua" || food.mealType == selectedMeal
        val matchSearch = search.isEmpty() || food.name.contains(search, ignoreCase = true)
        matchMeal && matchSearch
    }

    // ── Bottom Sheet: Tambah Makanan ──────────────────────────────────────
    var showAddFoodSheet by remember { mutableStateOf(false) }
    var newFoodName by remember { mutableStateOf("") }
    var newFoodEmoji by remember { mutableStateOf("") }
    var newFoodCalories by remember { mutableStateOf("") }
    var newFoodCarbs by remember { mutableStateOf("") }
    var newFoodProtein by remember { mutableStateOf("") }
    var newFoodFat by remember { mutableStateOf("") }
    var newFoodMealType by remember { mutableStateOf("Breakfast") }

    // ── Bottom Sheet: Simpan ke Database ─────────────────────────────────
    var showSaveDbSheet by remember { mutableStateOf(false) }
    var selectedFoodForDb by remember { mutableStateOf<Food?>(null) }
    var saveSuccess by remember { mutableStateOf(false) }

    // ── Bottom Sheet: Tambah Makanan Baru ─────────────────────────────────
    if (showAddFoodSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showAddFoodSheet = false
                newFoodName = ""; newFoodEmoji = ""; newFoodCalories = ""
                newFoodCarbs = ""; newFoodProtein = ""; newFoodFat = ""
                newFoodMealType = "Breakfast"
            },
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
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Tambah Makanan",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Buat entri makanan kustommu",
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
                                    showAddFoodSheet = false
                                    newFoodName = ""; newFoodEmoji = ""; newFoodCalories = ""
                                    newFoodCarbs = ""; newFoodProtein = ""; newFoodFat = ""
                                    newFoodMealType = "Breakfast"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(color = SlateLight)
                }

                // Pilih Emoji
                item {
                    Text("Pilih Ikon Makanan", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    val foodEmojis = listOf("🍚", "🍛", "🍜", "🥗", "🍗", "🥩", "🐟", "🥚", "🥣", "🥞", "🍕", "🍔", "🌮", "🥙", "🥤", "☕", "🍎", "🍌", "🥑", "🧆")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        modifier = Modifier.height(168.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false
                    ) {
                        items(foodEmojis) { emoji ->
                            val isSelected = newFoodEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) HealthGreen.copy(0.2f) else SlateLighter)
                                    .border(1.dp, if (isSelected) HealthGreen else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { newFoodEmoji = emoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 22.sp)
                            }
                        }
                    }
                }

                // Input Nama Makanan
                item {
                    OutlinedTextField(
                        value = newFoodName,
                        onValueChange = { newFoodName = it },
                        label = { Text("Nama Makanan") },
                        placeholder = { Text("Contoh: Gado-gado, Soto...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = foodTextFieldColors()
                    )
                }

                // Input Kalori
                item {
                    OutlinedTextField(
                        value = newFoodCalories,
                        onValueChange = { newFoodCalories = it.filter { c -> c.isDigit() } },
                        label = { Text("Kalori (kcal)") },
                        placeholder = { Text("Contoh: 350", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = foodTextFieldColors()
                    )
                }

                // Input Makro (Karbo, Protein, Lemak)
                item {
                    Text("Informasi Gizi (gram)", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newFoodCarbs,
                            onValueChange = { newFoodCarbs = it.filter { c -> c.isDigit() } },
                            label = { Text("Karbo (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = foodTextFieldColors()
                        )
                        OutlinedTextField(
                            value = newFoodProtein,
                            onValueChange = { newFoodProtein = it.filter { c -> c.isDigit() } },
                            label = { Text("Protein (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = foodTextFieldColors()
                        )
                        OutlinedTextField(
                            value = newFoodFat,
                            onValueChange = { newFoodFat = it.filter { c -> c.isDigit() } },
                            label = { Text("Lemak (g)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = foodTextFieldColors()
                        )
                    }
                }

                // Pilih Tipe Makan
                item {
                    Text("Tipe Makan", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    val mealTypeOptions = listOf(
                        Pair("🥣", "Breakfast"),
                        Pair("🍛", "Lunch"),
                        Pair("🍽️", "Dinner"),
                        Pair("🍎", "Snack")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        mealTypeOptions.forEach { (emoji, type) ->
                            val isSelected = newFoodMealType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) HealthGreen.copy(0.18f) else SlateLighter)
                                    .border(1.dp, if (isSelected) HealthGreen else Color.Transparent, RoundedCornerShape(12.dp))
                                    .clickable { newFoodMealType = type }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(emoji, fontSize = 18.sp)
                                    Spacer(Modifier.height(3.dp))
                                    Text(
                                        type,
                                        color = if (isSelected) HealthGreen else TextSecondary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Tombol Simpan
                item {
                    val canSave = newFoodName.isNotBlank() && newFoodEmoji.isNotEmpty() && newFoodCalories.isNotBlank()
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
                                val newFood = Food(
                                    id = foodList.size + 1,
                                    name = newFoodName.trim(),
                                    emoji = newFoodEmoji,
                                    calories = newFoodCalories.toIntOrNull() ?: 0,
                                    carbs = newFoodCarbs.toFloatOrNull() ?: 0f,
                                    protein = newFoodProtein.toFloatOrNull() ?: 0f,
                                    fat = newFoodFat.toFloatOrNull() ?: 0f,
                                    mealType = newFoodMealType
                                )
                                foodList.add(0, newFood)
                                showAddFoodSheet = false
                                newFoodName = ""; newFoodEmoji = ""; newFoodCalories = ""
                                newFoodCarbs = ""; newFoodProtein = ""; newFoodFat = ""
                                newFoodMealType = "Breakfast"
                                // Auto-buka sheet simpan ke DB
                                selectedFoodForDb = newFood
                                showSaveDbSheet = true
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
                                "Tambahkan Makanan",
                                color = if (canSave) DeepNavy else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Bottom Sheet: Simpan ke Database ─────────────────────────────────
    if (showSaveDbSheet) {
        val food = selectedFoodForDb
        ModalBottomSheet(
            onDismissRequest = {
                showSaveDbSheet = false
                selectedFoodForDb = null
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
                            "Simpan data makanan ke server",
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
                                showSaveDbSheet = false
                                selectedFoodForDb = null
                                saveSuccess = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = SlateLight)

                // Preview data
                Text("Data yang Akan Disimpan", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateLighter),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (food != null) {
                            NutritionDbPreviewRow("Makanan", "${food.emoji} ${food.name}")
                            NutritionDbPreviewRow("Kalori", "${food.calories} kcal")
                            NutritionDbPreviewRow("Karbohidrat", "${food.carbs.toInt()} g")
                            NutritionDbPreviewRow("Protein", "${food.protein.toInt()} g")
                            NutritionDbPreviewRow("Lemak", "${food.fat.toInt()} g")
                            NutritionDbPreviewRow("Tipe Makan", food.mealType)
                        } else {
                            NutritionDbPreviewRow("Pengguna", DummyData.currentUser.name)
                            NutritionDbPreviewRow("Total Kalori Hari Ini", "${foodList.take(5).sumOf { it.calories }} kcal")
                            NutritionDbPreviewRow("Tanggal", "Hari ini")
                        }
                        NutritionDbPreviewRow("Pengguna", DummyData.currentUser.name)
                    }
                }

                // Status koneksi
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
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AccentTeal)
                        )
                        Column {
                            Text("Database Terhubung", color = AccentTeal, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Siap menerima data nutrisi", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                if (saveSuccess) {
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
                                Text("Data makanan tersimpan ke database", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark)))
                            .clickable {
                                showSaveDbSheet = false
                                selectedFoodForDb = null
                                saveSuccess = false
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Selesai", color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark)))
                            .clickable { saveSuccess = true }
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
                        Text("Nutrisi", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("Pantau asupan harianmu", color = TextSecondary, fontSize = 13.sp)
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

        // ── Calorie Progress Card ─────────────────────────────────────────────
        item {
            val totalCalToday = foodList.take(5).sumOf { it.calories }
            val totalCarbToday = foodList.take(5).sumOf { it.carbs.toDouble() }.toFloat()
            val totalProtToday = foodList.take(5).sumOf { it.protein.toDouble() }.toFloat()
            val totalFatToday = foodList.take(5).sumOf { it.fat.toDouble() }.toFloat()

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
                                    "$totalCalToday",
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
                            "${((totalCalToday.toFloat() / DummyData.currentUser.targetCalories) * 100).toInt()}%",
                            color = HealthGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { (totalCalToday.toFloat() / DummyData.currentUser.targetCalories).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = HealthGreen,
                        trackColor = SlateLight
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MacroStat("🥦 Karbo", "${totalCarbToday.toInt()}g", HealthGreen)
                        MacroStat("🥩 Protein", "${totalProtToday.toInt()}g", AccentTeal)
                        MacroStat("🧈 Lemak", "${totalFatToday.toInt()}g", AccentSage)
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

        // ── Food List Header ──────────────────────────────────────────────────
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${filteredFoods.size} item",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    // Tombol Tambah Makanan
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(HealthGreen.copy(0.12f))
                            .border(1.dp, HealthGreen.copy(0.3f), RoundedCornerShape(10.dp))
                            .clickable { showAddFoodSheet = true }
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
            var added by remember(food.id) { mutableStateOf(false) }
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
                    // Tombol Simpan ke DB
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AccentTeal.copy(0.15f))
                            .clickable {
                                selectedFoodForDb = food
                                showSaveDbSheet = true
                                saveSuccess = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CloudUpload,
                            contentDescription = "Simpan ke DB",
                            tint = AccentTeal,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    // Tombol Tambah/Centang
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
private fun foodTextFieldColors() = OutlinedTextFieldDefaults.colors(
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

@Composable
private fun NutritionDbPreviewRow(label: String, value: String) {
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
private fun MacroStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = TextMuted, fontSize = 10.sp)
    }
}