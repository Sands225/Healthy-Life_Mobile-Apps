package com.example.healthylife.data

import com.example.healthylife.model.Exercise
import com.example.healthylife.model.Food
import com.example.healthylife.model.SleepRecord
import com.example.healthylife.model.User
import com.example.healthylife.util.DateUtils

object DummyData {

    val currentUser = User(
        id = 1,
        name = "Sandika",
        age = 22,
        weight = 68f,
        height = 172f,
        targetCalories = 2000,
        targetSleepHours = 8f,
        targetExerciseMinutes = 45
    )

    val exercises = listOf(
        Exercise(1, "Running",  "🏃", 35, 320, DateUtils.getTodayDateString()),
        Exercise(2, "Walking",  "🚶", 20, 90,  DateUtils.getTodayDateString()),
        Exercise(3, "Gym",      "🏋️", 50, 450, DateUtils.getRelativeDateString(1)),
        Exercise(4, "Yoga",     "🧘", 30, 120, DateUtils.getRelativeDateString(1)),
        Exercise(5, "Running",  "🏃", 40, 360, DateUtils.getRelativeDateString(2)),
        Exercise(6, "Cycling",  "🚴", 45, 380, DateUtils.getRelativeDateString(9))
    )

    val foods = listOf(
        Food(1, "Oatmeal + Pisang",  "🥣", 350, 60f, 12f, 6f,  8f, "Sarapan",        DateUtils.getTodayDateString()),
        Food(2, "Susu Rendah Lemak", "🥛", 110, 12f, 8f,  2f,  0f, "Sarapan",        DateUtils.getTodayDateString()),
        Food(3, "Nasi Goreng Ayam",  "🍳", 480, 55f, 22f, 14f, 3f, "Makan Siang",    DateUtils.getTodayDateString()),
        Food(4, "Kopi Kenangan",     "☕", 120, 18f, 3f,  4f,  0f, "Makanan Ringan", DateUtils.getTodayDateString()),
        Food(5, "Ayam Geprek",       "🍗", 520, 40f, 35f, 20f, 4f, "Makan Malam",    DateUtils.getTodayDateString()),
        Food(6, "Mie Gacoan",        "🍜", 380, 50f, 15f, 12f, 3f, "Makan Siang",    DateUtils.getRelativeDateString(2)),
        Food(7, "Buah Potong",       "🍉", 95,  22f, 1f,  0f,  4f, "Makanan Ringan", DateUtils.getRelativeDateString(3)),
        Food(8, "Salad Sayur",       "🥗", 210, 18f, 8f,  10f, 6f, "Makan Malam",    DateUtils.getRelativeDateString(9))
    )

    val sleepRecords = listOf(
        SleepRecord(1, DateUtils.getTodayDateString(),     "", "", 8.0f, "Baik"),
        SleepRecord(2, DateUtils.getRelativeDateString(1), "", "", 7.0f, "Cukup"),
        SleepRecord(3, DateUtils.getRelativeDateString(2), "", "", 7.0f, "Cukup"),
        SleepRecord(4, DateUtils.getRelativeDateString(3), "", "", 8.5f, "Baik"),
        SleepRecord(5, DateUtils.getRelativeDateString(4), "", "", 6.0f, "Buruk"),
        SleepRecord(6, DateUtils.getRelativeDateString(5), "", "", 8.0f, "Baik"),
        SleepRecord(7, DateUtils.getRelativeDateString(9), "", "", 7.0f, "Cukup")
    )

    val lastNightSleep = sleepRecords.first()

    val weekDayLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
}
