package com.example.healthylife.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    // Format yang digunakan untuk penyimpanan: YYYY-MM-DD
    private fun getFormatter(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
    }

    /**
     * Mendapatkan tanggal hari ini dalam format YYYY-MM-DD
     */
    fun getTodayDateString(): String {
        return getFormatter().format(Date())
    }

    /**
     * Mendapatkan tanggal relatif dalam format YYYY-MM-DD
     */
    fun getRelativeDateString(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return getFormatter().format(calendar.time)
    }

    /** Kalender dengan waktu dinormalisasi ke tengah malam. Null jika parsing gagal. */
    private fun midnightCalendar(dateStr: String): Calendar? {
        return try {
            val parsed = getFormatter().parse(dateStr) ?: return null
            Calendar.getInstance().apply {
                time = parsed
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            null
        }
    }

    /** Awal minggu ini (Senin, tengah malam). */
    private fun startOfThisWeek(): Calendar {
        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return cal
    }

    /** Apakah tanggal ini adalah hari ini. */
    fun isToday(dateStr: String): Boolean {
        val target = midnightCalendar(dateStr) ?: return false
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return target.timeInMillis == today.timeInMillis
    }

    /** Apakah tanggal ini berada di dalam minggu ini (Senin–Minggu). */
    fun isThisWeek(dateStr: String): Boolean {
        val target = midnightCalendar(dateStr) ?: return false
        val start = startOfThisWeek()
        val end = (start.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 7) }
        return target.timeInMillis >= start.timeInMillis && target.timeInMillis < end.timeInMillis
    }

    /** Apakah tanggal ini berada sebelum minggu ini (minggu-minggu sebelumnya). */
    fun isBeforeThisWeek(dateStr: String): Boolean {
        val target = midnightCalendar(dateStr) ?: return false
        return target.timeInMillis < startOfThisWeek().timeInMillis
    }

    private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000

    /** Indeks hari dalam minggu ini: 0 = Senin .. 6 = Minggu, atau -1 jika bukan minggu ini. */
    fun dayIndexInThisWeek(dateStr: String): Int {
        val target = midnightCalendar(dateStr) ?: return -1
        val start = startOfThisWeek()
        val diff = (target.timeInMillis - start.timeInMillis) / MILLIS_PER_DAY
        return if (diff in 0..6) diff.toInt() else -1
    }

    /** Tanggal awal minggu (Senin, format YYYY-MM-DD) dari sebuah tanggal. Null jika parsing gagal. */
    fun weekStartString(dateStr: String): String? {
        val target = midnightCalendar(dateStr) ?: return null
        target.firstDayOfWeek = Calendar.MONDAY
        // Mundur ke Senin
        while (target.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            target.add(Calendar.DAY_OF_YEAR, -1)
        }
        return getFormatter().format(target.time)
    }

    /** Format tanggal pendek "dd MMM" (contoh: 07 Jul). */
    fun formatDayMonth(dateStr: String): String {
        return try {
            val parsed = getFormatter().parse(dateStr) ?: return dateStr
            SimpleDateFormat("dd MMM", Locale("in", "ID")).format(parsed)
        } catch (e: Exception) {
            dateStr
        }
    }

    /**
     * Mengubah string tanggal (YYYY-MM-DD) menjadi teks relatif (Hari ini, Kemarin, x hari lalu)
     */
    fun toRelativeString(dateStr: String): String {
        return try {
            val parsedDate = getFormatter().parse(dateStr) ?: return dateStr
            
            // Normalisasi tanggal ke tengah malam untuk perhitungan selisih hari yang akurat
            val todayCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            val targetCal = Calendar.getInstance().apply {
                time = parsedDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            val diffInMillis = todayCal.timeInMillis - targetCal.timeInMillis
            val daysBetween = diffInMillis / (24 * 60 * 60 * 1000)
            
            when {
                daysBetween == 0L -> "Hari ini"
                daysBetween == 1L -> "Kemarin"
                daysBetween > 1L -> "$daysBetween hari lalu"
                daysBetween < 0L -> "Mendatang"
                else -> SimpleDateFormat("dd MMM yyyy", Locale("in", "ID")).format(parsedDate)
            }
        } catch (e: Exception) {
            // Jika gagal parsing (misal dummy data lama), kembalikan string aslinya
            dateStr
        }
    }
}
