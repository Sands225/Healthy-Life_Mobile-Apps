package com.example.healthylife.util

/**
 * Filter/mode rentang waktu untuk daftar riwayat & analitik
 * (Olahraga, Makanan, Tidur).
 */
enum class TimeFilter(val label: String) {
    HARI_INI("Hari Ini"),
    MINGGU_INI("Minggu Ini"),
    SEMUA("Semua");

    /** Apakah tanggal (format YYYY-MM-DD) cocok dengan rentang ini. */
    fun matches(dateStr: String): Boolean = when (this) {
        HARI_INI   -> DateUtils.isToday(dateStr)
        MINGGU_INI -> DateUtils.isThisWeek(dateStr)
        SEMUA      -> true
    }
}
