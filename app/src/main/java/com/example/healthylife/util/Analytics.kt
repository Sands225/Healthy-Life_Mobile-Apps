package com.example.healthylife.util

/**
 * Agregasi data untuk kartu analitik (Hari Ini / Minggu Ini / Semua).
 * Input: pasangan (tanggal YYYY-MM-DD, nilai).
 */
object Analytics {

    val weekDayLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

    fun todayTotal(data: List<Pair<String, Float>>): Float =
        data.filter { DateUtils.isToday(it.first) }.sumOf { it.second.toDouble() }.toFloat()

    fun todayCount(data: List<Pair<String, Float>>): Int =
        data.count { DateUtils.isToday(it.first) }

    /** 7 nilai (Sen–Min) minggu ini + label hari. */
    fun weekly(data: List<Pair<String, Float>>): Pair<List<Float>, List<String>> {
        val buckets = FloatArray(7)
        data.forEach { (date, value) ->
            val idx = DateUtils.dayIndexInThisWeek(date)
            if (idx in 0..6) buckets[idx] += value
        }
        return buckets.toList() to weekDayLabels
    }

    /** Agregasi per minggu (maks 7 minggu terakhir) + label W#. */
    fun allWeeks(data: List<Pair<String, Float>>): Pair<List<Float>, List<String>> {
        val perWeek = sortedMapOf<String, Float>()
        data.forEach { (date, value) ->
            val ws = DateUtils.weekStartString(date) ?: return@forEach
            perWeek[ws] = (perWeek[ws] ?: 0f) + value
        }
        val points = perWeek.entries.toList().takeLast(7)
        return points.map { it.value } to points.map { DateUtils.weekOfYearLabel(it.key) }
    }
}
