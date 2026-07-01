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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.view.ViewGroup
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.compose.ui.platform.ComposeView
import com.example.healthylife.data.DummyData
import com.example.healthylife.data.HealthRepository
import com.example.healthylife.model.Exercise
import com.example.healthylife.ui.theme.*
import com.example.healthylife.util.DateUtils

// Template aktivitas untuk shortcut / fast-add
private data class ActivityTemplate(
    val emoji: String,
    val name: String,
    val caloriesPerMin: Int
)

private class ExerciseViewHolder(val composeView: ComposeView) : RecyclerView.ViewHolder(composeView)

private class ExerciseAdapter(
    private var items: List<Exercise>,
    private var isDarkTheme: Boolean,
    private val onEdit: (Exercise) -> Unit,
    private val onDelete: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseViewHolder>() {

    fun updateTheme(darkTheme: Boolean) {
        if (isDarkTheme != darkTheme) {
            isDarkTheme = darkTheme
            notifyDataSetChanged()
        }
    }

    fun updateItems(newItems: List<Exercise>) {
        if (this.items != newItems) {
            items = newItems
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val composeView = ComposeView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return ExerciseViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val item = items[position]
        holder.composeView.setContent {
            CompositionLocalProvider(
                LocalDarkTheme provides isDarkTheme
            ) {
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    ExerciseHistoryItem(item, onEdit, onDelete)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

// ─────────────────────────────────────────────────────────────────────────────
// ExerciseScreen
// Header · Summary Hari Ini · Riwayat · FAB Tambah
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(padding: PaddingValues, repository: HealthRepository) {

    // ── Data riwayat ─────────────────────────────────────────────────────────
    val exerciseHistory = remember { mutableStateListOf<Exercise>() }

    LaunchedEffect(Unit) {
        val dbExercises = repository.getAllExercises()
        exerciseHistory.clear()
        exerciseHistory.addAll(dbExercises.ifEmpty { DummyData.exercises })
    }

    // ── Template aktivitas (shortcut) — bisa ditambah dari "Buat Shortcut" ──
    val activityTemplates = remember {
        mutableStateListOf(
            ActivityTemplate("🏃", "Running",  10),
            ActivityTemplate("🚶", "Walking",  4),
            ActivityTemplate("🧘", "Yoga",     4),
            ActivityTemplate("🏋️", "Gym",     9),
            ActivityTemplate("🚴", "Cycling",  8),
            ActivityTemplate("🏊", "Swimming", 11)
        )
    }

    // ── Bottom-sheet control ──────────────────────────────────────────────────
    var showAddSheet by remember { mutableStateOf(false) }
    var editingExercise by remember { mutableStateOf<Exercise?>(null) }
    var deletingExercise by remember { mutableStateOf<Exercise?>(null) }

    // ── Render Sheet ──────────────────────────────────────────────────────────
    if (showAddSheet) {
        AddActivitySheet(
            templates = activityTemplates,
            onDismiss = { showAddSheet = false },
            onLogActivity = { exercise ->
                val id = repository.insertExercise(exercise)
                exerciseHistory.add(0, exercise.copy(id = id.toInt()))
                showAddSheet = false
            }
        )
    }

    // ── Render Dialogs ────────────────────────────────────────────────────────
    editingExercise?.let { ex ->
        EditExerciseDialog(
            exercise = ex,
            onDismiss = { editingExercise = null },
            onConfirm = { updated ->
                repository.updateExercise(updated)
                val idx = exerciseHistory.indexOfFirst { it.id == updated.id }
                if (idx != -1) {
                    exerciseHistory[idx] = updated
                }
                editingExercise = null
            }
        )
    }

    deletingExercise?.let { ex ->
        DeleteConfirmDialog(
            exercise = ex,
            onDismiss = { deletingExercise = null },
            onConfirm = {
                repository.deleteExercise(ex.id)
                exerciseHistory.remove(ex)
                deletingExercise = null
            }
        )
    }

    // ── Main UI ───────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize()) {
        if (exerciseHistory.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
            ) {
                ExerciseHeader()
                Spacer(Modifier.height(20.dp))
                TodaySummaryCard(exerciseHistory)
                Spacer(Modifier.height(28.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Riwayat Olahraga",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = AccentTeal.copy(0.12f)
                    ) {
                        Text(
                            "0 sesi",
                            color = AccentTeal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyHistoryState()
                }
            }
        } else {
            val isDarkTheme = LocalDarkTheme.current
            val density = androidx.compose.ui.platform.LocalDensity.current

            var headerAndSummaryHeightPx by remember { mutableIntStateOf(0) }
            var titleHeightPx by remember { mutableIntStateOf(0) }
            var scrollOffset by remember { mutableIntStateOf(0) }

            val collapseOffset = with(density) {
                scrollOffset.coerceIn(0, headerAndSummaryHeightPx).toDp()
            }
            val bottomPaddingPx = with(density) { (padding.calculateBottomPadding() + 88.dp).roundToPx() }
            val topSystemPaddingPx = with(density) { padding.calculateTopPadding().roundToPx() }

            AndroidView(
                factory = { context ->
                    RecyclerView(context).apply {
                        layoutManager = LinearLayoutManager(context)
                        clipToPadding = false
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        adapter = ExerciseAdapter(
                            items = exerciseHistory.toList(),
                            isDarkTheme = isDarkTheme,
                            onEdit = { editingExercise = it },
                            onDelete = { deletingExercise = it }
                        )

                        var currentScroll = 0
                        addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                currentScroll += dy
                                scrollOffset = currentScroll
                            }
                        })
                    }
                },
                update = { recyclerView ->
                    val totalTopPaddingPx = headerAndSummaryHeightPx + titleHeightPx + topSystemPaddingPx
                    if (recyclerView.paddingTop != totalTopPaddingPx || recyclerView.paddingBottom != bottomPaddingPx) {
                        recyclerView.setPadding(0, totalTopPaddingPx, 0, bottomPaddingPx)
                    }
                    val adapter = recyclerView.adapter as? ExerciseAdapter
                    adapter?.updateTheme(isDarkTheme)
                    adapter?.updateItems(exerciseHistory.toList())
                },
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = padding.calculateTopPadding())
                    .offset(y = -collapseOffset)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coords ->
                            headerAndSummaryHeightPx = coords.size.height
                        }
                ) {
                    ExerciseHeader()
                    Spacer(Modifier.height(20.dp))
                    TodaySummaryCard(exerciseHistory)
                    Spacer(Modifier.height(28.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coords ->
                            titleHeightPx = coords.size.height
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Riwayat Olahraga",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = AccentTeal.copy(0.12f)
                        ) {
                            Text(
                                "${exerciseHistory.size} sesi",
                                color = AccentTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        // FAB — Tambah Aktivitas (pojok kanan bawah, di atas bottom navbar)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 24.dp,
                    bottom = padding.calculateBottomPadding() + 16.dp
                )
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark)))
                    .clickable { showAddSheet = true }
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = DeepNavy, modifier = Modifier.size(20.dp))
                    Text(
                        "Tambah Aktivitas",
                        color = DeepNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// AddActivitySheet — Bottom Sheet Tambah Aktivitas
// Berisi: Tab "Quick Add" (shortcut) & "Manual"
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddActivitySheet(
    templates: List<ActivityTemplate>,
    onDismiss: () -> Unit,
    onLogActivity: (Exercise) -> Unit
) {
    // Tab: 0 = Quick Add, 1 = Manual
    var activeTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Slate,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Sheet Header ──────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Tambah Aktivitas",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Pilih cara menambahkan aktivitasmu",
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

            // ── Tab Selector ──────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SlateLighter)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "⚡ Quick Add" to 0,
                    "✏️ Manual Add" to 1
                ).forEach { (label, idx) ->
                    val selected = activeTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selected) HealthGreen else Color.Transparent)
                            .clickable { activeTab = idx }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = if (selected) DeepNavy else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            HorizontalDivider(color = SlateLight)

            // ── Tab Content ───────────────────────────────────────────────────
            when (activeTab) {
                0 -> QuickAddTab(
                    templates = templates,
                    onLog = onLogActivity
                )
                1 -> ManualAddTab(onLog = onLogActivity)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab 0: Quick Add — pilih shortcut lalu log langsung
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun QuickAddTab(
    templates: List<ActivityTemplate>,
    onLog: (Exercise) -> Unit
) {
    var selected by remember { mutableStateOf<ActivityTemplate?>(null) }
    var duration by remember { mutableFloatStateOf(30f) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Pilih Aktivitas", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)

        // Grid shortcut aktivitas
        val rowCount = Math.ceil(templates.size / 3.0).toInt()
        val gridHeight = (rowCount * 84 + (rowCount - 1) * 10).dp
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .height(gridHeight)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            userScrollEnabled = false
        ) {
            items(templates) { t ->
                val isSelected = selected == t
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected)
                                Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))
                            else
                                Brush.linearGradient(listOf(SlateLighter, SlateLighter))
                        )
                        .then(
                            if (!isSelected) Modifier.border(1.dp, SlateLight, RoundedCornerShape(14.dp))
                            else Modifier
                        )
                        .clickable { selected = t }
                        .padding(horizontal = 10.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(t.emoji, fontSize = 24.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            t.name,
                            color = if (isSelected) DeepNavy else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Slider durasi (muncul jika ada yang dipilih)
        if (selected != null) {
            HorizontalDivider(color = SlateLight)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SlateLighter),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Durasi", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            "${duration.toInt()} menit",
                            color = HealthGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Slider(
                        value = duration,
                        onValueChange = { duration = it },
                        valueRange = 5f..120f,
                        colors = SliderDefaults.colors(
                            thumbColor = HealthGreen,
                            activeTrackColor = HealthGreen,
                            inactiveTrackColor = SlateLight
                        )
                    )
                    // Quick preset
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(15f, 30f, 45f, 60f).forEach { m ->
                            val isP = duration == m
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isP) HealthGreen else Slate)
                                    .clickable { duration = m }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    "${m.toInt()}m",
                                    color = if (isP) DeepNavy else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Estimasi kalori
                    val cals = (duration * (selected?.caloriesPerMin ?: 0)).toInt()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentTeal.copy(0.08f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🔥", fontSize = 16.sp)
                        Text("Estimasi kalori terbakar:", color = TextSecondary, fontSize = 12.sp)
                        Spacer(Modifier.weight(1f))
                        Text("~$cals kcal", color = AccentTeal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // Tombol Log Sekarang
            val t = selected!!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(HealthGreen, HealthGreenDark)))
                    .clickable {
                        val cals = (duration * t.caloriesPerMin).toInt()
                        onLog(
                            Exercise(
                                id = 0,
                                name = t.name,
                                emoji = t.emoji,
                                durationMinutes = duration.toInt(),
                                caloriesBurned = cals,
                                date = DateUtils.getTodayDateString()
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
                    Icon(Icons.Default.CheckCircle, null, tint = DeepNavy)
                    Text(
                        "Log ${t.name} · ${duration.toInt()} mnt",
                        color = DeepNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            // Hint saat belum pilih
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateLight.copy(0.5f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("👆 Pilih aktivitas di atas untuk mulai", color = TextMuted, fontSize = 13.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tab 1: Manual Add — isi nama, emoji, durasi, kalori sendiri
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ManualAddTab(onLog: (Exercise) -> Unit) {
    var activityName by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("🏃") }
    var durationText by remember { mutableStateOf("") }
    var caloriesText by remember { mutableStateOf("") }

    val emojiOptions = listOf("🏃","🚶","🧘","🏋️","🚴","🏊","⛷️","🤸","🥊","🏸","⚽","🎾","🧗","🤽","🏇")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

        // Pilih emoji
        Text("Pilih Ikon", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.height(165.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(emojiOptions) { emoji ->
                val isSel = selectedEmoji == emoji
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSel) HealthGreen.copy(0.2f) else SlateLighter)
                        .border(1.dp, if (isSel) HealthGreen else Color.Transparent, RoundedCornerShape(12.dp))
                        .clickable { selectedEmoji = emoji },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 22.sp)
                }
            }
        }

        // Nama aktivitas
        ExerciseTextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = "Nama Aktivitas",
            placeholder = "Contoh: Zumba, Badminton..."
        )

        // Durasi & Kalori (baris berdampingan)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ExerciseTextField(
                value = durationText,
                onValueChange = { durationText = it.filter(Char::isDigit) },
                label = "Durasi (menit)",
                placeholder = "30",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            ExerciseTextField(
                value = caloriesText,
                onValueChange = { caloriesText = it.filter(Char::isDigit) },
                label = "Kalori (kcal)",
                placeholder = "150",
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }

        // Tombol Simpan
        val canLog = activityName.isNotBlank() && durationText.isNotBlank()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (canLog)
                        Brush.linearGradient(listOf(HealthGreen, HealthGreenDark))
                    else
                        Brush.linearGradient(listOf(SlateLight, SlateLight))
                )
                .clickable(enabled = canLog) {
                    onLog(
                        Exercise(
                            id = 0,
                            name = activityName.trim(),
                            emoji = selectedEmoji,
                            durationMinutes = durationText.toIntOrNull() ?: 30,
                            caloriesBurned = caloriesText.toIntOrNull() ?: 0,
                            date = DateUtils.getTodayDateString()
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
                Icon(Icons.Default.Save, null, tint = if (canLog) DeepNavy else TextMuted)
                Text(
                    "Simpan Aktivitas",
                    color = if (canLog) DeepNavy else TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}



@Composable
private fun ExerciseHeader() {
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
                Text("Exercise", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("Pantau aktivitas fisikmu hari ini", color = TextSecondary, fontSize = 13.sp)
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

@Composable
private fun TodaySummaryCard(exerciseHistory: List<Exercise>) {
    val todayLogs = exerciseHistory.filter { it.date == DateUtils.getTodayDateString() }
    val totalMinutes = todayLogs.sumOf { it.durationMinutes }
    val totalCalories = todayLogs.sumOf { it.caloriesBurned }
    val totalSessions = todayLogs.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Slate)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(HealthGreen)
                )
                Text(
                    "Aktivitas Hari Ini",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                ExerciseStat("⏱️", "$totalMinutes", "menit", "Durasi", HealthGreen)
                Box(Modifier.width(1.dp).height(50.dp).background(SlateLight))
                ExerciseStat("🔥", "$totalCalories", "kcal", "Kalori", AccentTeal)
                Box(Modifier.width(1.dp).height(50.dp).background(SlateLight))
                ExerciseStat("💪", "$totalSessions", "sesi", "Sesi", AccentSage)
            }
        }
    }
}

@Composable
private fun ExerciseHistoryItem(
    ex: Exercise,
    onEdit: (Exercise) -> Unit,
    onDelete: (Exercise) -> Unit
) {
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
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(HealthGreen.copy(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(ex.emoji, fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(ex.name, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    "${ex.durationMinutes} menit · ${ex.caloriesBurned} kcal",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SlateLight
                ) {
                    Text(
                        DateUtils.toRelativeString(ex.date),
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opsi",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit(ex)
                            },
                            leadingIcon = { Icon(Icons.Default.Edit, null, tint = AccentTeal) }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onDelete(ex)
                            },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🏃", fontSize = 48.sp)
            Text(
                "Belum ada riwayat olahraga",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Tekan tombol + untuk mulai catat aktivitasmu",
                color = TextMuted,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ExerciseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = TextMuted) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
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
    )
}

@Composable
private fun ExerciseStat(emoji: String, value: String, unit: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 18.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(unit, color = color.copy(0.7f), fontSize = 10.sp)
        Text(label, color = TextMuted, fontSize = 9.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditExerciseDialog(
    exercise: Exercise,
    onDismiss: () -> Unit,
    onConfirm: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf(exercise.name) }
    var emoji by remember { mutableStateOf(exercise.emoji) }
    var durationText by remember { mutableStateOf(exercise.durationMinutes.toString()) }
    var caloriesText by remember { mutableStateOf(exercise.caloriesBurned.toString()) }
    
    val emojiOptions = listOf("🏃","🚶","🧘","🏋️","🚴","🏊","⛷️","🤸","🥊","🏸","⚽","🎾","🧗","🤽","🏇")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Edit Aktivitas", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Pilih Ikon
                Text("Pilih Ikon", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(115.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    userScrollEnabled = true
                ) {
                    items(emojiOptions) { em ->
                        val isSel = emoji == em
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) HealthGreen.copy(0.2f) else SlateLighter)
                                .border(1.dp, if (isSel) HealthGreen else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { emoji = em },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(em, fontSize = 18.sp)
                        }
                    }
                }

                // Nama Aktivitas
                Text("Nama Aktivitas", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = HealthGreen,
                        unfocusedBorderColor = SlateLight,
                        focusedContainerColor = Slate,
                        unfocusedContainerColor = Slate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Durasi
                Text("Durasi (Menit)", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = HealthGreen,
                        unfocusedBorderColor = SlateLight,
                        focusedContainerColor = Slate,
                        unfocusedContainerColor = Slate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Kalori
                Text("Estimasi Kalori Terbakar (kcal)", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = HealthGreen,
                        unfocusedBorderColor = SlateLight,
                        focusedContainerColor = Slate,
                        unfocusedContainerColor = Slate
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: exercise.durationMinutes
                    val calories = caloriesText.toIntOrNull() ?: exercise.caloriesBurned
                    if (name.isNotBlank()) {
                        onConfirm(
                            exercise.copy(
                                name = name.trim(),
                                emoji = emoji,
                                durationMinutes = duration,
                                caloriesBurned = calories
                            )
                        )
                    }
                }
            ) {
                Text("Simpan", color = HealthGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        containerColor = Slate,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun DeleteConfirmDialog(
    exercise: Exercise,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Hapus Aktivitas", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Text("Apakah kamu yakin ingin menghapus catatan aktivitas \"${exercise.name}\"? Tindakan ini tidak bisa dibatalkan.", color = TextSecondary)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        containerColor = Slate,
        shape = RoundedCornerShape(20.dp)
    )
}