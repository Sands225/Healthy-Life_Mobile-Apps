package com.example.healthylife.data

import com.example.healthylife.model.Exercise
import com.example.healthylife.model.Food
import com.example.healthylife.model.SleepRecord
import com.example.healthylife.model.User

object DummyData {

    val currentUser = User(
        id = 1,
        name = "Sandika",
        age = 22,
        weight = 68f,
        height = 172f,
        targetCalories = 2000,
        targetSleepHours = 8f,
        targetExerciseMinutes = 45,
        streakDays = 12
    )

    val exercises = listOf(
        Exercise(1, "Running",  "🏃", 35, 320, "Hari ini"),
        Exercise(2, "Walking",  "🚶", 20, 90,  "Hari ini"),
        Exercise(3, "Gym",      "🏋️", 50, 450, "Kemarin"),
        Exercise(4, "Yoga",     "🧘", 30, 120, "Kemarin"),
        Exercise(5, "Running",  "🏃", 40, 360, "2 hari lalu"),
        Exercise(6, "Cycling",  "🚴", 45, 380, "3 hari lalu")
    )

    val todayExercises = exercises.filter { it.date == "Hari ini" }
    val todayExerciseMinutes = todayExercises.sumOf { it.durationMinutes }
    val todayCaloriesBurned  = todayExercises.sumOf { it.caloriesBurned }

    val foods = listOf(
        Food(1, "Oatmeal + Pisang",  "🥣", 350, 60f, 12f, 6f,  "Breakfast"),
        Food(2, "Nasi Goreng Ayam",  "🍳", 480, 55f, 22f, 14f, "Lunch"),
        Food(3, "Mie Gacoan",        "🍜", 380, 50f, 15f, 12f, "Lunch"),
        Food(4, "Ayam Geprek",       "🍗", 520, 40f, 35f, 20f, "Dinner"),
        Food(5, "Kopi Kenangan",     "☕", 120, 18f, 3f,  4f,  "Snack"),
        Food(6, "Buah Potong",       "🍉", 95,  22f, 1f,  0f,  "Snack"),
        Food(7, "Salad Sayur",       "🥗", 210, 18f, 8f,  10f, "Dinner"),
        Food(8, "Susu Rendah Lemak", "🥛", 110, 12f, 8f,  2f,  "Breakfast")
    )

    val todayFoods = foods.take(5)  // makanan hari ini
    val totalCaloriesToday = todayFoods.sumOf { it.calories }        // 1330
    val totalCarbsToday    = todayFoods.sumOf { it.carbs.toDouble() }.toFloat()
    val totalProteinToday  = todayFoods.sumOf { it.protein.toDouble() }.toFloat()
    val totalFatToday      = todayFoods.sumOf { it.fat.toDouble() }.toFloat()

    val sleepRecords = listOf(
        SleepRecord(1, "Hari ini",   "22:00", "06:00", 8.0f,  "Excellent"),
        SleepRecord(2, "Kemarin",    "23:30", "06:30", 7.0f,  "Normal"),
        SleepRecord(3, "2 hari lalu","22:45", "05:45", 7.0f,  "Normal"),
        SleepRecord(4, "3 hari lalu","21:30", "06:00", 8.5f,  "Excellent"),
        SleepRecord(5, "4 hari lalu","00:00", "06:00", 6.0f,  "Poor"),
        SleepRecord(6, "5 hari lalu","22:15", "06:15", 8.0f,  "Excellent"),
        SleepRecord(7, "6 hari lalu","23:00", "06:00", 7.0f,  "Normal")
    )

    val lastNightSleep = sleepRecords.first()
    val avgSleepHours  = sleepRecords.map { it.durationHours }.average().toFloat()

    val weeklyExerciseData = listOf(35, 70, 0, 50, 30, 45, 35)   // menit per hari (Sen–Min)
    val weeklyCaloriesData = listOf(1800, 2100, 1500, 2000, 1900, 2200, 1330) // kcal
    val weekDayLabels = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

    val smartInsights = listOf(
        "Target mingguan kamu hampir tercapai! Tetap semangat 💪",
        "Tidurmu rata-rata ${String.format("%.1f", avgSleepHours)} jam — sangat baik!",
        "Kamu sudah membakar ${todayCaloriesBurned} kcal hari ini 🔥",
        "Streak ${currentUser.streakDays} hari berturut-turut! Luar biasa! 🏆"
    )
}