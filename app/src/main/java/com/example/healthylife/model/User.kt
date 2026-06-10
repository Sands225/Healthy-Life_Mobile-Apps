package com.example.healthylife.model

data class SleepRecord(
    val id: Int,
    val date: String,
    val bedTime: String,
    val wakeTime: String,
    val durationHours: Float,
    val quality: String  // Excellent, Normal, Poor
)

data class User(
    val id: Int,
    val name: String,
    val age: Int,
    val weight: Float,
    val height: Float,
    val targetCalories: Int,
    val targetSleepHours: Float,
    val targetExerciseMinutes: Int,
    val streakDays: Int
)