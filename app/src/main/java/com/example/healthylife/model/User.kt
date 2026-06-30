package com.example.healthylife.model

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