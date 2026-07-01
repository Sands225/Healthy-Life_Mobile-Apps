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
