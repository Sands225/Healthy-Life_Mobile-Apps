package com.example.healthylife.model

data class Exercise(
    val id: Int,
    val name: String,
    val emoji: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val date: String
)