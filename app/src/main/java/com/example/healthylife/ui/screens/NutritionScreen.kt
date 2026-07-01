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
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.model.Food
import com.example.healthylife.ui.componenets.TimeFilterRow
import com.example.healthylife.ui.theme.*
import com.example.healthylife.util.DateUtils
import com.example.healthylife.util.TimeFilter

// 4 kategori makanan
private val mealCategories = listOf(
    "🥣" to "Sarapan",
    "🍛" to "Makan Siang",
    "🍽️" to "Makan Malam",
    "🍎" to "Makanan Ringan"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(padding: PaddingValues, repository: HealthRepository) {

    var search by remember { mutableStateOf("") }
    var selectedMeal by remember { mutableStateOf("Semua") }
    var timeFilter by remember { mutableStateOf(TimeFilter.HARIAN) }

    var user by remember { mutableStateOf(DummyData.currentUser) }

    val foodList = remember { mutableStateListOf<Food>() }

    fun reload() {
        val dbFoods = repository.getAllFoods()
        foodList.clear()
        foodList.addAll(dbFoods.ifEmpty { DummyData.foods })
    }

    LaunchedEffect(Unit) {
        repository.getUser(1)?.let { user = it }
        reload()
    }

    // Filter chip makanan: Semua + 4 kategori
    val mealFilters = listOf("🍽️" to "Semua") + mealCategories

    val filteredFoods = foodList.filter { food ->
        val matchTime = timeFilter.matches(food.date)
        val matchMeal = selectedMeal == "Semua" || food.mealType == selectedMeal
        val matchSearch = search.isEmpty() || food.name.contains(search, ignoreCase = true)
        matchTime && matchMeal && matchSearch
    }

    // Total hari ini (selalu berdasarkan hari ini, apa pun filternya)
    val todayFoods = foodList.filter { DateUtils.isToday(it.date) }
    val totalCalToday = todayFoods.sumOf { it.calories }
    val totalCarbToday = todayFoods.sumOf { it.carbs.toDouble() }.toFloat()
    val totalProtToday = todayFoods.sumOf { it.protein.toDouble() }.toFloat()
    val totalFatToday = todayFoods.sumOf { it.fat.toDouble() }.toFloat()
    val totalFiberToday = todayFoods.sumOf { it.fiber.toDouble() }.toFloat()

    // Sheet & dialog
    var showFormSheet by remember { mutableStateOf(false) }
    var editingFood by remember { mutableStateOf<Food?>(null) }
    var deletingFood by remember { mutableStateOf<Food?>(null) }

    if (showFormSheet) {
        FoodFormSheet(
            initial = editingFood,
            onDismiss = {
                showFormSheet = false
                editingFood = null
            },
            onSave = { food ->
                if (food.id == 0) {
                    repository.insertFood(food)
                } else {
                    repository.updateFood(food)
                }
                reload()
                showFormSheet = false
                editingFood = null
            }
        )
    }

    deletingFood?.let { food ->
        AlertDialog(
            onDismissRequest = { deletingFood = null },
            title = { Text("Hapus Makanan", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Hapus \"${food.name}\" dari daftar makanan?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    repository.deleteFood(food.id)
                    reload()
                    deletingFood = null
                }) { Text("Hapus", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deletingFood = null }) { Text("Batal", color = TextSecondary) }
            },
            containerColor = Slate,
            shape = RoundedCornerShape(20.dp)
        )
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
                    .background(Brush.verticalGradient(listOf(HeaderStart, DeepNavy)))
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

        // ── Kalori Hari Ini ───────────────────────────────────────────────────
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
                                Text("$totalCalToday", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                                Text(" / ${user.targetCalories} kcal", color = TextSecondary, fontSize = 13.sp)
                            }
                        }
                        Text(
                            "${((totalCalToday.toFloat() / user.targetCalories) * 100).toInt()}%",
                            color = HealthGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { (totalCalToday.toFloat() / user.targetCalories).coerceIn(0f, 1f) },
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
                        MacroStat("🌾 Serat", "${totalFiberToday.toInt()}g", CardPink)
                    }
                }
            }
        }

        // ── Filter Waktu ──────────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(24.dp))
            Text(
                "Rentang Waktu",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            TimeFilterRow(selected = timeFilter, onSelected = { timeFilter = it })
        }

        // ── Filter Kategori ───────────────────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Text(
                "Kategori",
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
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                mealFilters.forEach { (emoji, label) ->
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
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(emoji, fontSize = 16.sp)
                            Spacer(Modifier.height(3.dp))
                            Text(
                                label,
                                color = if (isSelected) HealthGreen else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }

        // ── Pencarian ─────────────────────────────────────────────────────────
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
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted) },
                trailingIcon = {
                    if (search.isNotEmpty()) {
                        Icon(Icons.Default.Clear, null, tint = TextMuted, modifier = Modifier.clickable { search = "" })
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

        // ── Header Daftar + tombol Tambah ─────────────────────────────────────
        item {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daftar Makanan", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${filteredFoods.size} item", color = TextMuted, fontSize = 12.sp)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(HealthGreen.copy(0.12f))
                            .border(1.dp, HealthGreen.copy(0.3f), RoundedCornerShape(10.dp))
                            .clickable {
                                editingFood = null
                                showFormSheet = true
                            }
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp),
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
                            .background(HealthGreen.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(food.emoji, fontSize = 20.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(food.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(
                            "${food.calories} kcal  ·  K:${food.carbs.toInt()}g  P:${food.protein.toInt()}g  L:${food.fat.toInt()}g  S:${food.fiber.toInt()}g",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(food.mealType, color = AccentTeal, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            Text("·", color = TextMuted, fontSize = 10.sp)
                            Text(DateUtils.toRelativeString(food.date), color = TextMuted, fontSize = 10.sp)
                        }
                    }
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opsi", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { showMenu = false; editingFood = food; showFormSheet = true },
                                leadingIcon = { Icon(Icons.Default.Edit, null, tint = AccentTeal) }
                            )
                            DropdownMenuItem(
                                text = { Text("Hapus", color = Color.Red) },
                                onClick = { showMenu = false; deletingFood = food },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Sheet: Tambah / Edit Makanan
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodFormSheet(
    initial: Food?,
    onDismiss: () -> Unit,
    onSave: (Food) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var emoji by remember { mutableStateOf(initial?.emoji ?: "") }
    var calories by remember { mutableStateOf(initial?.calories?.toString() ?: "") }
    var carbs by remember { mutableStateOf(initial?.carbs?.toInt()?.toString() ?: "") }
    var protein by remember { mutableStateOf(initial?.protein?.toInt()?.toString() ?: "") }
    var fat by remember { mutableStateOf(initial?.fat?.toInt()?.toString() ?: "") }
    var fiber by remember { mutableStateOf(initial?.fiber?.toInt()?.toString() ?: "") }
    var mealType by remember { mutableStateOf(initial?.mealType ?: "Sarapan") }

    val isEdit = initial != null && initial.id != 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            if (isEdit) "Edit Makanan" else "Tambah Makanan",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (isEdit) "Perbarui data makanan" else "Buat entri makanan baru",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SlateLight)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(color = SlateLight)
            }

            // Emoji
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
                    items(foodEmojis) { em ->
                        val isSelected = emoji == em
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) HealthGreen.copy(0.2f) else SlateLighter)
                                .border(1.dp, if (isSelected) HealthGreen else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { emoji = em },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(em, fontSize = 22.sp)
                        }
                    }
                }
            }

            // Nama
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Makanan") },
                    placeholder = { Text("Contoh: Gado-gado, Soto...", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = foodTextFieldColors()
                )
            }

            // Kalori
            item {
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it.filter { c -> c.isDigit() } },
                    label = { Text("Kalori (kcal)") },
                    placeholder = { Text("Contoh: 350", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = foodTextFieldColors()
                )
            }

            // Makro: Karbo, Protein, Lemak, Serat
            item {
                Text("Informasi Gizi (gram)", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NutritionMacroField(carbs, { carbs = it }, "Karbo", Modifier.weight(1f))
                    NutritionMacroField(protein, { protein = it }, "Protein", Modifier.weight(1f))
                    NutritionMacroField(fat, { fat = it }, "Lemak", Modifier.weight(1f))
                    NutritionMacroField(fiber, { fiber = it }, "Serat", Modifier.weight(1f))
                }
            }

            // Kategori (4)
            item {
                Text("Kategori Makan", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    mealCategories.forEach { (em, type) ->
                        val isSelected = mealType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) HealthGreen.copy(0.18f) else SlateLighter)
                                .border(1.dp, if (isSelected) HealthGreen else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { mealType = type }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(em, fontSize = 18.sp)
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    type,
                                    color = if (isSelected) HealthGreen else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }

            // Simpan
            item {
                val canSave = name.isNotBlank() && emoji.isNotEmpty() && calories.isNotBlank()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (canSave) Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))
                            else Brush.linearGradient(listOf(SlateLight, SlateLight))
                        )
                        .clickable(enabled = canSave) {
                            onSave(
                                Food(
                                    id = initial?.id ?: 0,
                                    name = name.trim(),
                                    emoji = emoji,
                                    calories = calories.toIntOrNull() ?: 0,
                                    carbs = carbs.toFloatOrNull() ?: 0f,
                                    protein = protein.toFloatOrNull() ?: 0f,
                                    fat = fat.toFloatOrNull() ?: 0f,
                                    fiber = fiber.toFloatOrNull() ?: 0f,
                                    mealType = mealType,
                                    date = initial?.date ?: DateUtils.getTodayDateString()
                                )
                            )
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            if (isEdit) Icons.Default.Save else Icons.Default.Add,
                            null,
                            tint = if (canSave) DeepNavy else TextMuted
                        )
                        Text(
                            if (isEdit) "Simpan Perubahan" else "Tambahkan Makanan",
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

@Composable
private fun NutritionMacroField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
        label = { Text(label, fontSize = 10.sp) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = foodTextFieldColors()
    )
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
private fun MacroStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = TextMuted, fontSize = 10.sp)
    }
}
