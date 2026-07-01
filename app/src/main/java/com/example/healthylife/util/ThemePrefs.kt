package com.example.healthylife.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * Menyimpan & menerapkan preferensi mode gelap/terang menggunakan
 * AppCompatDelegate (cara native View/XML).
 */
object ThemePrefs {

    private const val PREFS = "healthylife_prefs"
    private const val KEY_DARK = "dark_mode"

    fun isDark(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        // Default: mode gelap (sesuai desain awal)
        return prefs.getBoolean(KEY_DARK, true)
    }

    /** Terapkan preferensi tersimpan (dipanggil saat aplikasi mulai). */
    fun apply(context: Context) {
        setMode(isDark(context))
    }

    /** Ganti mode & simpan; Activity akan otomatis dibuat ulang. */
    fun toggle(context: Context) {
        val newValue = !isDark(context)
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK, newValue)
            .apply()
        setMode(newValue)
    }

    private fun setMode(dark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
